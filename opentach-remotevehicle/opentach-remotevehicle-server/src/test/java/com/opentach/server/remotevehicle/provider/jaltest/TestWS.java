package com.opentach.server.remotevehicle.provider.jaltest;

import java.util.Date;

import com.opentach.common.remotevehicle.provider.jaltest.JaltestException;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.JaltestInvoker;

public class TestWS {
	public static void main(String[] args) {
		try {
			// https://swjttodfapi020.jaltest.com/WsJaltestTelematicsAPI.svc/soap/
			// https://test_swjttodfapi020.jaltest.com/WsJaltestTelematicsAPI.svc/soap/
			JaltestInvoker jaltestInvoker = new JaltestInvoker("https://swjttodfapi020.jaltest.com/WsJaltestTelematicsAPI.svc/soap/",
					"8129C9DC9F70BD75752BF48E668D638050BD4DCE", "0ABF35056BA1E18FB690F4DE28C639EBD6B09B76");
			String cif = "CIF1_TEST";
			String plateNumber = "7682-HMR_TEST";
			jaltestInvoker.createFleet("Test02", cif, "Avda bla", "699666666", "test02@opentach.com", "00001", new Date(), null, "Test02");
			System.out.println("Fleet created ");
			String generateCompanyKeys = jaltestInvoker.generateCompanyKeys(cif);
			System.out.println("Generated key " + generateCompanyKeys);
			jaltestInvoker.createVehicle(cif, "M", plateNumber);
			System.out.println("Vehicle created ");
			jaltestInvoker.setupVehicleTelematicUnit(cif, "TEST_ECU_4", plateNumber);
			System.out.println("Telematic unit pair ");
			jaltestInvoker.stablishVehicleDownloadPeriod(cif, plateNumber, true);
			Integer startVehicleDownload = jaltestInvoker.startVehicleDownload(cif, plateNumber, null, null);
			System.out.println("Start vehicle download " + startVehicleDownload);
		} catch (JaltestException e) {
			e.printStackTrace();
		}
	}
}
