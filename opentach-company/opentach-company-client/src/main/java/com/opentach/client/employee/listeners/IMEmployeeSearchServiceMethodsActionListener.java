package com.opentach.client.employee.listeners;

import java.awt.event.ActionEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import javax.swing.AbstractButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.tools.classpath.ClassPathSeeker;

public class IMEmployeeSearchServiceMethodsActionListener extends AbstractActionListenerButton {

	private static final Logger logger = LoggerFactory.getLogger(IMEmployeeSearchServiceMethodsActionListener.class);

	public IMEmployeeSearchServiceMethodsActionListener() throws Exception {
		super();
	}

	public IMEmployeeSearchServiceMethodsActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
		// TODO Auto-generated constructor stub
	}

	public IMEmployeeSearchServiceMethodsActionListener(Hashtable params) throws Exception {
		super(params);
		// TODO Auto-generated constructor stub
	}

	public IMEmployeeSearchServiceMethodsActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		new Thread() {
			@Override
			public void run() {
				List<List<String>> classesList = ClassPathSeeker.find((pckgName, file) -> {
					List<String> res = new ArrayList<>();

					if (IMEmployeeSearchServiceMethodsActionListener.this.isPackageNameCompliant(pckgName)) {
						if (file.startsWith("/")) {
							file = file.substring(1);
						}
						if (file.endsWith("Service.class") || file.endsWith("Service2.class")) {
							try {
								String className = file.substring(0, file.length() - 6);
								String fullClassName = pckgName + "." + className;
								Class<?> cl = Class.forName(fullClassName);
								if (cl.isInterface()) {
									Method[] declaredMethods = cl.getDeclaredMethods();
									for (Method method : declaredMethods) {

										String newMethod = fullClassName + "/" + method.getName();
										if (!res.contains(newMethod)) {
											res.add(newMethod);
										}

									}
								}
							} catch (ClassNotFoundException error) {
								IMEmployeeSearchServiceMethodsActionListener.logger.error(null, error);
							}
						}
					}
					return res.isEmpty() ? null : res;
				});

				List<String> methodList = IMEmployeeSearchServiceMethodsActionListener.this.sortMethodsInClasses(classesList);
				IMEmployeeSearchServiceMethodsActionListener.this.dumpServiceMethods(methodList);
			}
		}.start();

	}

	private boolean isPackageNameCompliant(String pckg) {
		String pckgName = pckg.toLowerCase();

		return pckgName.startsWith("com.imatia") || //
				pckgName.startsWith("com.ontimize") || //
				pckgName.contains("tacolab") || //
				pckgName.contains("tacholab") || //
				pckgName.contains("opentach");
	}

	private List<String> sortMethodsInClasses(List<List<String>> classesList) {
		List<String> sortedList = new ArrayList<>();

		for (List<String> aClass : classesList) {
			for (String aMethod : aClass) {
				sortedList.add(aMethod);
			}
		}
		Collections.sort(sortedList);

		return sortedList;
	}

	private void dumpServiceMethods(List<String> methodsList) {

		System.out.println("delete from usr_rol_server_permission;");
		System.out.println("delete from usr_server_permission;");

		String template = "insert into usr_server_permission(srp_name) values ('%s');";

		for (String string : methodsList) {
			System.out.println(String.format(template, string));
		}

		System.out.println("insert into usr_rol_server_permission (nivel_cd, srp_id) select nivel_cd, srp_id from cdniveles, usr_server_permission;");
	}
}
