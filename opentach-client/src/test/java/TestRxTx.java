import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.RXTXVersion;

import java.util.Enumeration;

public class TestRxTx {

	public static void main(String[] args) {
		System.out.println(RXTXVersion.nativeGetVersion());
		Enumeration en = CommPortIdentifier.getPortIdentifiers();
		while (en.hasMoreElements()) {
			System.out.println(((CommPort) en.nextElement()).getName());
		}
	}

}
