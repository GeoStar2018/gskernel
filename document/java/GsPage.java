package unittest;

/**
 * 纸张对象
 * @author hekai
 *	A0=841*1189mm
 *	A1=594*841mm
 *	A2=420*594mm
 *  A3=297*420mm
 *  A4=210*297mm
 */
public class GsPage {
	public GsPage(GsPage page)
	{
		Name = page.Name;
		Width = page.Width;
		Height = page.Height;
	}
	public GsPage(String name,int w,int h)
	{
		Name = name;
		Width = w;
		Height = h;
	}
	public String Name;
	public int Width;
	public int Height;
}
