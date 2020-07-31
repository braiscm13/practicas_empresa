package com.opentach.client.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Enumeration;

import javax.swing.FocusManager;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.builder.xml.XMLApplicationBuilder;
import com.ontimize.gui.Application;
import com.ontimize.gui.ApplicationLauncher;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.FixedFocusManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.MessageDialog;
import com.ontimize.gui.table.TableAttribute;
import com.ontimize.jee.desktopclient.builder.MultiModuleApplicationLauncher;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.jee.desktopclient.spring.BeansFactory;
import com.ontimize.report.ReportManager;
import com.ontimize.xml.DefaultXMLParametersManager;
import com.opentach.client.report.CustomDynamicJasperEngine;
import com.opentach.common.exception.OpentachRuntimeException;
import com.utilmize.client.tools.FxUtils;

import javafx.application.Platform;

public class OpentachApplicationLauncher {

	private static final Logger logger = LoggerFactory.getLogger(OpentachApplicationLauncher.class);

	public static void checkLibraries() {
		new Thread("Check Libraries") {
			@Override
			public void run() {
				super.run();
				try {
					// com.ontimize.report.ReportManager.createReportEngine();
					// Ontimize lanza esto en un hilo y me impide cambiarlo, de ahí el sleep
					Thread.sleep(4000);
					ReportManager.registerNewReportEngine(new CustomDynamicJasperEngine());
				} catch (final Exception error) {
					OpentachApplicationLauncher.logger.error(null, error);
				}
			}
		}.start();
	}

	public static void main(String[] args) {
		Platform.setImplicitExit(false);
		FxUtils.ensureFX();
		MessageManager.setMessageManager(new OpentachMessageManager());
		OpentachApplicationLauncher.checkLibraries();
		if ((args == null) || (args.length < 2)) {
			System.out.println(
					"Syntax: ApplicationLauncher 'xmlLabelFile' 'xmlApplicationFile' ['package'] [-d(for debug)] [-https/http (for https/http tunneling)] [-nathttp/nathttps (for https/http tunneling using NAT socket factory)] [-ssl -sslnoreconnect]. \nThe first and the second parameters must include the complete path relative to the classpath");
			System.exit(-1);
		}

		args = ApplicationLauncher.configureSystemProperties(args);

		try {
			final String jws16wa = System.getProperty("com.ontimize.gui.disabled.jws16wa");
			if ("true".equalsIgnoreCase(jws16wa) == false) {
				sun.rmi.server.LoaderHandler.registerCodebaseLoader(TableAttribute.class.getClassLoader());
			}
		} catch (final Error e) {
		} catch (final Exception e) {
		}

		String lf = System.getProperty("com.ontimize.gui.lafclassname");
		if (lf == null) {
			lf = ApplicationLauncher.START_LOOK_AND_FEEL;
		}

		try {
			UIManager.setLookAndFeel(lf);
		} catch (final Exception err) {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (final Exception ex) {
				OpentachApplicationLauncher.logger.error(null, err);
				OpentachApplicationLauncher.logger.error(null, ex);
			}
		}

		final String sLabelsFile = args[0];
		final String sXMLFile = args[1];
		final String sPackageName = args.length == 2 ? "" : !args[2].startsWith("-") ? args[2] : "";

		for (final String arg : args) {
			if ((arg != null) && arg.equalsIgnoreCase("-d")) {
				Form.RELOAD_BUTTON_VISIBLE = true;
				ApplicationManager.DEBUG = true;
				ApplicationManager.setApplicationManagerWindowVisible(true);
				break;
			}
		}

		for (int i = 0; i < args.length; i++) {
			if ((i < (args.length - 1)) && (args[i] != null) && args[i].equalsIgnoreCase("-conf") && (args[i + 1] != null)) {
				DefaultXMLParametersManager.setXMLDefaultParameterFile(args[i + 1]);
				break;
			}
		}

		for (final String arg : args) {
			if ((arg != null) && arg.equalsIgnoreCase("-https")) {
				if (java.rmi.server.RMISocketFactory.getSocketFactory() != null) {
					if ((java.rmi.server.RMISocketFactory.getSocketFactory() instanceof com.ontimize.util.rmitunneling.RMIHTTPSTunnelingSocketFactory) == false) {
						// There is a socket factory yet, different from the
						// factory that we need.
						// Remove the factory
						OpentachApplicationLauncher.removeRMISocketFactory();
					}
				}

				// Properties
				final String cgipath = System.getProperty("com.ontimize.util.rmitunneling.cgipath");
				final String port = System.getProperty("com.ontimize.util.rmitunneling.port");
				try {
					if ((cgipath != null) && (port != null)) {
						java.rmi.server.RMISocketFactory.setSocketFactory(new com.ontimize.util.rmitunneling.RMIHTTPSTunnelingSocketFactory(cgipath, Integer.parseInt(port)));
						System.out.println("Installed RMIHTTPSTunnelingSocketFactory " + cgipath + " : " + port);
					} else if (port != null) {
						java.rmi.server.RMISocketFactory.setSocketFactory(new com.ontimize.util.rmitunneling.RMIHTTPSTunnelingSocketFactory(Integer.parseInt(port)));
						System.out.println("Installed RMIHTTPSTunnelingSocketFactory  : " + port);
					} else {
						java.rmi.server.RMISocketFactory.setSocketFactory(new com.ontimize.util.rmitunneling.RMIHTTPSTunnelingSocketFactory());
						System.out.println("Installed RMIHTTPSTunnelingSocketFactory ");
					}
				} catch (final java.io.IOException e) {
					e.printStackTrace();
					throw new IllegalArgumentException(
							"RMIHTTPSTunnelingSocketFactory cannot be established" + e.getMessage() + "  -> " + java.rmi.server.RMISocketFactory.getSocketFactory());
				} catch (final Exception e) {
					e.printStackTrace();
					throw new IllegalArgumentException(
							"RMIHTTPSTunnelingSocketFactory cannot be established" + e.getMessage() + "  -> " + java.rmi.server.RMISocketFactory.getSocketFactory());
				}
			} else if ((arg != null) && arg.equalsIgnoreCase("-http")) {
				if (java.rmi.server.RMISocketFactory.getSocketFactory() != null) {
					if ((java.rmi.server.RMISocketFactory.getSocketFactory() instanceof com.ontimize.util.rmitunneling.RMIHTTPTunnelingSocketFactory) == false) {
						// There is a socket factory yet, different from the
						// factory
						// that we need.
						// Remove the factory
						OpentachApplicationLauncher.removeRMISocketFactory();
					}
				}
				// Properties
				final String cgipath = System.getProperty("com.ontimize.util.rmitunneling.cgipath");
				final String port = System.getProperty("com.ontimize.util.rmitunneling.port");
				try {
					if ((cgipath != null) && (port != null)) {
						java.rmi.server.RMISocketFactory.setSocketFactory(new com.ontimize.util.rmitunneling.RMIHTTPTunnelingSocketFactory(cgipath, Integer.parseInt(port)));
						System.out.println("Install RMIHTTPTunnelingSocketFactory " + cgipath + " : " + port);
					} else if (port != null) {
						java.rmi.server.RMISocketFactory.setSocketFactory(new com.ontimize.util.rmitunneling.RMIHTTPTunnelingSocketFactory(Integer.parseInt(port)));
						System.out.println("Install RMIHTTPTunnelingSocketFactory  : " + port);
					} else {
						java.rmi.server.RMISocketFactory.setSocketFactory(new com.ontimize.util.rmitunneling.RMIHTTPTunnelingSocketFactory());
						System.out.println("Install RMIHTTPTunnelingSocketFactory ");
					}
				} catch (final java.io.IOException e) {
					e.printStackTrace();
					throw new IllegalArgumentException(
							"RMIHTTPTunnelingSocketFactory cannot be established " + e.getMessage() + "  -> " + java.rmi.server.RMISocketFactory.getSocketFactory());
				} catch (final Exception e) {
					e.printStackTrace();
					throw new IllegalArgumentException(
							"RMIHTTPTunnelingSocketFactory cannot be established " + e.getMessage() + "  -> " + java.rmi.server.RMISocketFactory.getSocketFactory());
				}
			} else if ((arg != null) && arg.equalsIgnoreCase("-ssl")) {
				// Properties
				try {
					boolean v14 = false;
					try {
						Class.forName("javax.net.ssl.SSLContext");
						v14 = true;
					} catch (final Exception ex) {
						v14 = false;
					}

					if (java.rmi.server.RMISocketFactory.getSocketFactory() != null) {
						if ((java.rmi.server.RMISocketFactory.getSocketFactory() instanceof com.ontimize.util.rmi.RMISSLSocketFactory) == false) {
							OpentachApplicationLauncher.removeRMISocketFactory();
						}
					}

					if (v14) {
						java.rmi.server.RMISocketFactory.setSocketFactory(com.ontimize.util.rmi.RMISSLReconnectSocketFactory
								.getInstance((com.ontimize.util.rmi.RMISSLSocketFactory) com.ontimize.util.rmi.RMISSLSocketFactory.getInstance(), true));
					} else {
						java.rmi.server.RMISocketFactory.setSocketFactory(com.ontimize.util.rmi.RMISSLReconnectSocketFactory
								.getInstance((com.ontimize.util.rmi.RMISSLSocketFactory) com.ontimize.util.rmi.RMISSLSocketFactory13.getInstance(), true));
					}
				} catch (final java.io.IOException e) {
					e.printStackTrace();
					throw new IllegalArgumentException("RMISSLReconnectSocketFactory cannot be established " + e.getMessage());
				} catch (final Exception e) {
					e.printStackTrace();
					throw new IllegalArgumentException("RMISSLReconnectSocketFactory cannot be established " + e.getMessage());
				}
			} else if ((arg != null) && arg.equalsIgnoreCase("-sslnoreconnect")) {
				// Properties
				try {
					boolean v14 = false;
					try {
						Class.forName("javax.net.ssl.SSLContext");
						v14 = true;
					} catch (final Exception ex) {
						v14 = false;
					}

					if (java.rmi.server.RMISocketFactory.getSocketFactory() != null) {
						if ((java.rmi.server.RMISocketFactory.getSocketFactory() instanceof com.ontimize.util.rmi.RMISSLSocketFactory) == false) {
							OpentachApplicationLauncher.removeRMISocketFactory();
						}
					}

					if (v14) {

						java.rmi.server.RMISocketFactory.setSocketFactory(com.ontimize.util.rmi.RMISSLReconnectSocketFactory
								.getInstance((com.ontimize.util.rmi.RMISSLSocketFactory) com.ontimize.util.rmi.RMISSLSocketFactory.getInstance(), false));
					} else {
						java.rmi.server.RMISocketFactory.setSocketFactory(com.ontimize.util.rmi.RMISSLReconnectSocketFactory
								.getInstance((com.ontimize.util.rmi.RMISSLSocketFactory) com.ontimize.util.rmi.RMISSLSocketFactory13.getInstance(), false));
					}
				} catch (final java.io.IOException e) {
					e.printStackTrace();
					throw new IllegalArgumentException("RMISSLReconnectSocketFactory cannot be established " + e.getMessage());
				} catch (final Exception e) {
					e.printStackTrace();
					throw new IllegalArgumentException("RMISSLReconnectSocketFactory cannot be established " + e.getMessage());
				}
			} else if ((arg != null) && arg.equalsIgnoreCase("-nathttp")) {
				if (java.rmi.server.RMISocketFactory.getSocketFactory() != null) {
					if ((java.rmi.server.RMISocketFactory.getSocketFactory() instanceof com.ontimize.util.rmitunneling.NatRMIHTTPTunnelingSocketFactory) == false) {
						// There is a socket factory yet, different from the
						// factory
						// that we need.
						// Remove the factory
						OpentachApplicationLauncher.removeRMISocketFactory();
					}
				}
				// Properties
				final String host = System.getProperty("com.ontimize.locator.ReferenceLocator.Hostname");
				final String cgipath = System.getProperty("com.ontimize.util.rmitunneling.cgipath");
				final String port = System.getProperty("com.ontimize.util.rmitunneling.port");
				try {
					if ((host != null) && (cgipath != null) && (port != null)) {
						java.rmi.server.RMISocketFactory
						.setSocketFactory(new com.ontimize.util.rmitunneling.NatRMIHTTPTunnelingSocketFactory(host, cgipath, Integer.parseInt(port)));
						System.out.println("Install NatRMIHTTPTunnelingSocketFactory -> " + "URL:" + host + cgipath + " : " + port);
					} else {
						throw new IllegalArgumentException(
								"NatRMIHTTPTunnelingSocketFactory cannot be established: property com.ontimize.locator.ReferenceLocator.Hostname is required for this factory");
					}
				} catch (final java.io.IOException e) {
					e.printStackTrace();
					throw new IllegalArgumentException(
							"NatRMIHTTPTunnelingSocketFactory cannot be established " + e.getMessage() + "  -> " + java.rmi.server.RMISocketFactory.getSocketFactory());
				} catch (final Exception e) {
					e.printStackTrace();
					throw new IllegalArgumentException(
							"NatRMIHTTPTunnelingSocketFactory cannot be established " + e.getMessage() + "  -> " + java.rmi.server.RMISocketFactory.getSocketFactory());
				}
			} else if ((arg != null) && arg.equalsIgnoreCase("-nathttps")) {
				if (java.rmi.server.RMISocketFactory.getSocketFactory() != null) {
					if ((java.rmi.server.RMISocketFactory.getSocketFactory() instanceof com.ontimize.util.rmitunneling.NatRMIHTTPSTunnelingSocketFactory) == false) {
						// There is a socket factory yet, different from the
						// factory
						// that we need.
						// Remove the factory
						OpentachApplicationLauncher.removeRMISocketFactory();
					}
				}
				// Properties
				final String host = System.getProperty("com.ontimize.locator.ReferenceLocator.Hostname");
				final String cgipath = System.getProperty("com.ontimize.util.rmitunneling.cgipath");
				final String port = System.getProperty("com.ontimize.util.rmitunneling.port");
				try {
					if ((host != null) && (cgipath != null) && (port != null)) {
						java.rmi.server.RMISocketFactory
						.setSocketFactory(new com.ontimize.util.rmitunneling.NatRMIHTTPSTunnelingSocketFactory(host, cgipath, Integer.parseInt(port)));
						System.out.println("Install NatRMIHTTPSTunnelingSocketFactory -> " + "URL:" + host + cgipath + " : " + port);
					}
				} catch (final java.io.IOException e) {
					e.printStackTrace();
					throw new IllegalArgumentException(
							"NatRMIHTTPSTunnelingSocketFactory cannot be established " + e.getMessage() + "  -> " + java.rmi.server.RMISocketFactory.getSocketFactory());
				} catch (final Exception e) {

					e.printStackTrace();
					throw new IllegalArgumentException(
							"NatRMIHTTPSTunnelingSocketFactory cannot be established " + e.getMessage() + "  -> " + java.rmi.server.RMISocketFactory.getSocketFactory());
				}
			}

		}

		new MultiModuleApplicationLauncher() {
			@Override
			protected void doInCreationThread(String labelsPath, String clientApplicationPath, String[] springConfigurationFiles, String[] args) {
				this.checkLibraries();

				BeansFactory.init(springConfigurationFiles);

				if (!ApplicationManager.jvmVersionHigherThan_1_4_0()) {
					OpentachApplicationLauncher.logger.info("FixedFocusManager established");
					FocusManager.setCurrentManager(new FixedFocusManager());
				}

				final URL urlLabelsFile = this.getClass().getClassLoader().getResource(labelsPath);
				{
					final URL urlXMLFile = this.getClass().getClassLoader().getResource(clientApplicationPath);
					if (urlLabelsFile == null) {
						System.out.println("'" + labelsPath + "' file cannot be found");
						try {
							final Enumeration enumResources = this.getClass().getClassLoader().getResources(labelsPath);
							while (enumResources.hasMoreElements()) {
								OpentachApplicationLauncher.logger.info("The following matching resources have been found: {}", enumResources.nextElement().toString());
							}
						} catch (final Exception err) {
							OpentachApplicationLauncher.logger.error(null, err);
						}
						MessageDialog.showErrorMessage(null, "'" + labelsPath + "' cannot be found");
						System.exit(-1);
					}
					if (urlXMLFile == null) {
						OpentachApplicationLauncher.logger.info("{} file cannot be found", clientApplicationPath);
						try {
							final Enumeration enumResources = this.getClass().getClassLoader().getResources(clientApplicationPath);
							while (enumResources.hasMoreElements()) {
								System.out.println("The following matching resources have been found: " + enumResources.nextElement().toString());
							}
						} catch (final Exception err) {
							OpentachApplicationLauncher.logger.error(null, err);
						}
						MessageDialog.showErrorMessage(null, "'" + clientApplicationPath + "' file cannot be found");
						System.exit(-1);
					}
				}

				try {
					final OpentachXMLApplicationBuilder applicationBuilder = new OpentachXMLApplicationBuilder(urlLabelsFile.toString(), sPackageName);
					XMLApplicationBuilder.setXMLApplicationBuilder(applicationBuilder);

					final Application application = applicationBuilder.buildApplication((!clientApplicationPath.startsWith("/") ? "/" : "") + clientApplicationPath);
					applicationBuilder.onApplicationBuilt(application);
					application.login();
					SwingUtilities.invokeAndWait(() -> application.show());
				} catch (final Exception err) {
					throw new OpentachRuntimeException(err);
				}
			}
		}.launch(new String[] { sLabelsFile, sXMLFile, "classpath*:spring-config-opentach.xml" });
	}

	/**
	 * Method used to fix the problem with java web start 1.5.0 when client has a proxy
	 */
	protected static void removeRMISocketFactory() {
		try {
			final Class socketFactoryClass = java.rmi.server.RMISocketFactory.class;
			final Field f = socketFactoryClass.getDeclaredField("factory");
			if (!Modifier.isStatic(f.getModifiers())) {
				throw new Exception("error-> factory variable not static");
			}
			f.setAccessible(true);
			f.set(f, null);
			OpentachApplicationLauncher.logger.info("Remove RMI Socket Factory finished");
		} catch (final Exception err) {
			OpentachApplicationLauncher.logger.error(null, err);
		}
	}

}
