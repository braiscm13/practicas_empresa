import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

class FrameLook {

	public static void showFrame(String plaf) {
		try {
			UIManager.setLookAndFeel(plaf);
		} catch(Exception e) {
			e.printStackTrace();
		}
		JFrame f = new JFrame(plaf);
		f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		f.setSize(400,100);
		f.setLocationByPlatform(true);
		JFrame.setDefaultLookAndFeelDecorated(true);
		f.setVisible(true);
	}

	public static void main(String[] args) {
		FrameLook.showFrame(UIManager.getSystemLookAndFeelClassName());
		FrameLook.showFrame(UIManager.getCrossPlatformLookAndFeelClassName());
		FrameLook.showFrame(NimbusLookAndFeel.class.getName());
	}
}