## ˢ������Ŵ�С��λת�������� ##

���磺�����ٵĶ�δ���ע��ʱ������DT�ĵ�λת����ʱ�������ȷ

ԭ�����ڻ��ƹ����У��ں˻Ὣ��ǰͼ��Ĳο����������õ�pDisplay.GsDisplayTransformation�У���ˣ�ͨ��pDisplay.GsDisplayTransformation�������С�Ǹ��ݵ�ǰͼ��Ĳο�������������ģ����Եõ��Ľ������ȷ,�ں˴������£�

```
bool GsLayer::Draw(GsDisplay* pDisplay,GsTrackCancel* pCancel,GsDrawPhase eDrawPhase)
{
	//�������Ҫ�����򲻻��ơ�
	if (!NeedDraw(pDisplay, eDrawPhase))
		return false;

	//����ο������ߡ�
	double dblScale = 0;
	if (!Utility::GsMath::IsEqual(m_dblReferenceScale, 0))
	{
		dblScale = pDisplay->DisplayTransformation()->ReferenceScale();
		pDisplay->DisplayTransformation()->ReferenceScale(m_dblReferenceScale);
	}

	CanvasClipRecovery tmpClip(pDisplay, this);

	bool b = InnerDraw(pDisplay,pCancel,eDrawPhase);
	
	if(!Utility::GsMath::IsEqual(m_dblReferenceScale,0))
		pDisplay->DisplayTransformation()->ReferenceScale(dblScale);
	return b;

}
```
���InnerDraw��������ִ���У��ϲ����µ���PointToMM�������õ��Ľ��������ȷ���������£�

```
pDisplay->DisplayTransformation()->PointToMM(9.0) \\�����У�ͬʱ��������������õ��Ľ������ȷ;
```
��ˣ��ڻ��ƹ����У��ϲ㲻Ӧ��ֱ������pDisplay.GsDisplayTransformation��


## ��ȷ��ʹ�÷��� ##
�������⣬�ϲ���Լ�ά��һ��GsDisplayTransformation�Ķ���ר�����ڽ���PointToMM���������
