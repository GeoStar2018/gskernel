#pragma once
#include "thread"
#include "vector"
#include "atomic"
#include "condition_variable"
#include "queue"
#include "future"

class GsThreadPool{
public:
	GsThreadPool(const size_t count)
	{
		m_iIdlThrNum = count < 1 ? 1 : count;
		m_vecThreadPool.reserve(count);
		for (std::size_t i = 0; i < m_iIdlThrNum; ++i)
		{
			m_vecThreadPool.emplace_back(
				[this] {
				while (m_bStoped)
				{
					std::function<void()> task;
					{
						std::unique_lock<std::mutex> lock(m_Lock);
						m_cvTask.wait(lock,
							[this] {
							return m_bStoped.load() || !m_queTasks.empty();
						});
						if (m_bStoped && m_queTasks.empty())
							return;
						task = std::move(m_queTasks.front());
						m_queTasks.pop();
					}
					--m_iIdlThrNum;
					task();
					++m_iIdlThrNum;
				}
			});
		}
	}

	GsThreadPool::~GsThreadPool()
	{
		m_bStoped.store(true);
		m_cvTask.notify_all();
		for (std::thread& t : m_vecThreadPool)
		{
			if (t.joinable())
				t.join();
		}
	}

	inline std::size_t GsThreadPool::IdlCount() const
	{
		return m_iIdlThrNum;
	}

	template<class F, class... Args>
	auto GsThreadPool::AddTask(F&& f, Args&&... args) ->std::future<decltype(f(args...))>
	{
		if (m_bStoped.load())
			throw std::runtime_error("commit on ThreadPoll is stoped");
		using RetType = typename std::result_of<F(Args...)>::type;
		auto task = std::make_shared<std::packaged_task<RetType>>(
			std::bind(std::forword<F>(f), std::forword<Args>(args)...));
		std::future<RetType> futrue = task->get_future();
		{
			std::lock_guard<std::mutex> lock(m_lock);
			task.emplace(
				[task]()
			{
				(*task)();
			}
			)
		}
		m_cvTask.notify_one();
		return futrue;
	}

private:
	using Task = std::function<void()>;
	std::vector<std::thread>	m_vecThreadPool;
	std::mutex					m_Lock;
	std::atomic<bool>			m_bStoped = false;
	std::condition_variable		m_cvTask;
	std::queue<Task>			m_queTasks;
	std::atomic<std::size_t>	m_iIdlThrNum;
};
