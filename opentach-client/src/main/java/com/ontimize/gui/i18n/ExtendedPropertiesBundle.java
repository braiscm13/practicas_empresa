package com.ontimize.gui.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.MainApplication;
import com.ontimize.locator.ClientReferenceLocator;
import com.ontimize.locator.EntityReferenceLocator;
import com.ontimize.locator.InitialContext;
import com.ontimize.locator.UtilReferenceLocator;
import com.ontimize.util.extend.EntitiesPropertiesParser;
import com.ontimize.util.extend.OrderProperties;

public class ExtendedPropertiesBundle extends ResourceBundle implements Serializable {

	private static final Logger		logger				= LoggerFactory.getLogger(ExtendedPropertiesBundle.class);

	public static final String		INCLUDE_KEY			= "@include";

	private static Hashtable		cache				= new Hashtable();

	protected static Hashtable		databaseCache;

	protected static Hashtable		extendedBundleCache	= new Hashtable();

	protected static List<String>	moduleResources		= new ArrayList<>();

	private static class CacheKey implements Serializable {

		protected String	baseName	= null;

		protected Locale	locale		= null;

		public CacheKey(String baseName, Locale l) {
			this.baseName = baseName;
			this.locale = l;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o instanceof CacheKey) {
				if (this.baseName.equals(((CacheKey) o).baseName) && this.locale.equals(((CacheKey) o).locale)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public int hashCode() {
			return this.baseName.hashCode() + this.locale.hashCode();
		}

		@Override
		public String toString() {
			return this.baseName + "_" + this.locale.toString();
		}

		public Locale getLocale() {
			return this.locale;
		}

	}

	private ResourceBundle[]	res			= null;

	private Vector				cachedKeys	= null;

	public ResourceBundle[] getBundles() {
		return this.res;
	}

	protected void setBundles(ResourceBundle[] bundles) {
		this.res = bundles;
	}

	protected void addBundle(ResourceBundle bundle) {
		int size = this.res != null ? this.res.length : 0;
		ResourceBundle[] result = new ResourceBundle[size + 1];
		if (this.res != null) {
			System.arraycopy(this.res, 0, result, 0, this.res.length);
		}
		result[size] = bundle;
		this.res = result;
	}

	protected void setBundles(List bundles) {
		this.res = new ResourceBundle[bundles.size()];
		for (int i = 0; i < bundles.size(); i++) {
			this.res[i] = (ResourceBundle) bundles.get(i);
		}

	}

	private ExtendedPropertiesBundle(Vector bundles) {
		this.res = new ResourceBundle[bundles.size()];
		for (int i = 0; i < bundles.size(); i++) {
			this.res[i] = (ResourceBundle) bundles.get(i);
		}
	}

	@Override
	public Locale getLocale() {
		if ((this.res == null) || (this.res.length == 0) || (this.res[0] == null)) {
			return super.getLocale();
		}
		return this.res[0].getLocale();
	}

	@Override
	public Enumeration getKeys() {
		if (this.cachedKeys == null) {
			Vector keys = new Vector();
			for (int i = 0; i < this.res.length; i++) {
				ResourceBundle r = this.res[i];
				if (r == null) {
					continue;
				}
				Enumeration ks = r.getKeys();
				while (ks.hasMoreElements()) {
					Object k = ks.nextElement();
					if (keys.contains(k) == false) {
						keys.add(k);
					}
				}
			}
			this.cachedKeys = keys;
		}
		return this.cachedKeys.elements();
	}

	@Override
	public Object handleGetObject(String key) {
		for (int i = 0; i < this.res.length; i++) {
			ResourceBundle r = this.res[i];
			if (r.containsKey(key)) {
				return r.getObject(key);
			}
		}
		// FIXED Slow when not key found, return own key and not null
		return key;
	}

	public static ResourceBundle getExtendedBundle(String baseName, Locale l) {

		CacheKey key = new CacheKey(baseName, l);
		if (ExtendedPropertiesBundle.cache.containsKey(key)) {
			ExtendedPropertiesBundle.logger.debug("Bundle {} in cache", baseName);
			ExtendedPropertiesBundle extendedPropertiesBundle = (ExtendedPropertiesBundle) ExtendedPropertiesBundle.cache.get(key);
			return extendedPropertiesBundle;
		}
		Vector v = ExtendedPropertiesBundle.getBundles(baseName, l);
		ExtendedPropertiesBundle b = new ExtendedPropertiesBundle(v);
		ExtendedPropertiesBundle.cache.put(key, b);
		return b;
	}

	private static Vector getBundles(String baseName, Locale l) {
		Vector v = new Vector();

		if (ExtendedPropertiesBundle.useDatabaseBundle) {
			Object object = ExtendedPropertiesBundle.databaseCache.get(new CacheKey(baseName, l));
			if (object == null) {
				Hashtable allDatabaseResourceBundles = ExtendedPropertiesBundle.getAllDatabaseResourceBundles(l);
				if (allDatabaseResourceBundles != null) {
					ExtendedPropertiesBundle.databaseCache.putAll(allDatabaseResourceBundles);
				}
			}
		}

		Object extendedBundle = ExtendedPropertiesBundle.extendedBundleCache.get(new CacheKey(baseName, l));
		if (extendedBundle == null) {
			Hashtable extendedResourceBundle = ExtendedPropertiesBundle.getExtendedResourceBundles(baseName, l);
			if (extendedResourceBundle != null) {
				ExtendedPropertiesBundle.extendedBundleCache.putAll(extendedResourceBundle);
			}
		}

		ExtendedPropertiesBundle.getBundles(baseName, l, v);
		// Include modules bundle...
		for (String moduleBundle : ExtendedPropertiesBundle.moduleResources) {
			ExtendedPropertiesBundle.getBundles(moduleBundle, l, v);
		}
		return v;
	}

	private static void getBundles(String baseName, Locale l, Vector dest) {
		ExtendedPropertiesBundle.logger.debug("Loading bundle: {}", baseName);
		// If the bundle is in cache then put it directly
		ResourceBundle bundle = null;
		CacheKey key = new CacheKey(baseName, l);

		if (ExtendedPropertiesBundle.cache.containsKey(key) == false) {
			try {
				if ((ExtendedPropertiesBundle.databaseCache != null) && ExtendedPropertiesBundle.databaseCache.containsKey(key)) {
					dest.add(ExtendedPropertiesBundle.databaseCache.get(key));
				}
				if ((ExtendedPropertiesBundle.extendedBundleCache != null) && ExtendedPropertiesBundle.extendedBundleCache.containsKey(key)) {
					dest.add(ExtendedPropertiesBundle.extendedBundleCache.get(key));
					ExtendedPropertiesBundle.checkImports((ResourceBundle) ExtendedPropertiesBundle.extendedBundleCache.get(key), l, dest);
				}
				bundle = ResourceBundle.getBundle(baseName, l);
			} catch (Exception e) {
				ExtendedPropertiesBundle.logger.error("Error loading bundle: {}", e.getMessage(), e);
			}
		} else {
			ExtendedPropertiesBundle.logger.debug("Bundle {} in cache");
			bundle = (ResourceBundle) ExtendedPropertiesBundle.cache.get(key);
		}

		dest.add(bundle);
		ExtendedPropertiesBundle.checkImports(bundle, l, dest);

	}

	protected static void checkImports(ResourceBundle bundle, Locale l, Vector dest) {
		if ((bundle instanceof PropertyResourceBundle) || (bundle instanceof ExtendedPropertiesResourceBundle)) {
			// Search the key '@include'
			try {
				String sIncludeBundle = bundle.getString(ExtendedPropertiesBundle.INCLUDE_KEY);
				if ((sIncludeBundle != null) && (sIncludeBundle.indexOf(';') > 0)) {
					StringTokenizer st = new StringTokenizer(sIncludeBundle, ";");
					while (st.hasMoreTokens()) {
						String bundleI = st.nextToken();
						ExtendedPropertiesBundle.logger.debug("Including: {}", bundleI);
						ExtendedPropertiesBundle.getBundles(bundleI, l, dest);
					}
				} else {
					ExtendedPropertiesBundle.logger.debug("Including: {}", sIncludeBundle);
					ExtendedPropertiesBundle.getBundles(sIncludeBundle, l, dest);
				}
			} catch (Exception e) {
				ExtendedPropertiesBundle.logger.trace("{}", e.getMessage(), e);
			}
		}
	}

	private static Hashtable getExtendedResourceBundles(String baseName, Locale l) {
		Properties extendedBundleProperties = new Properties();

		StringBuilder builder = new StringBuilder();
		String baseNameWithSlashes = baseName.replace('.', '/');
		builder.append(baseNameWithSlashes);
		builder.append("_extends_");
		builder.append(l);
		builder.append(".properties");
		String _extendsBundle = builder.toString();

		try {
			Enumeration<URL> _extends = EntitiesPropertiesParser.class.getClassLoader().getResources(builder.toString());

			if (_extends.hasMoreElements()) {

				EntitiesPropertiesParser parser = new EntitiesPropertiesParser();
				List<OrderProperties> extendsResourceBundle = new ArrayList<>();

				while (_extends.hasMoreElements()) {
					Properties extendedBundle = new Properties();
					URL urlStream = _extends.nextElement();
					InputStream stream = urlStream.openStream();
					extendedBundle.clear();
					extendedBundle.load(stream);
					int index = parser.parseExtendPropertiesOrder(extendedBundle);
					extendsResourceBundle.add(new OrderProperties(index, extendedBundle));
				}

				Collections.sort(extendsResourceBundle);
				Properties extendedBundle = new Properties();

				for (OrderProperties entry : extendsResourceBundle) {
					extendedBundle = entry.getProperties();
					extendedBundleProperties = parser.parseProperties(extendedBundleProperties, extendedBundle);
					ExtendedPropertiesBundle.logger.debug("Resource bundle \"{}\" with locale \"{}\" extended, Load order -> {}", baseName, l, entry.getIndex());
				}

				Hashtable toRet = new Hashtable();
				ExtendedPropertiesResourceBundle extendedProperties = new ExtendedPropertiesResourceBundle(new Hashtable(extendedBundleProperties), l);
				toRet.put(new CacheKey(baseName, l), extendedProperties);

				return toRet;
			}
		} catch (IOException e) {
			ExtendedPropertiesBundle.logger.error("Resource bundle \"{}\" with locale \"{}\" can't be extended. ERROR: {}", baseName, l, e.getMessage(), e);
		}

		return null;
	}

	public static int getSizeInMemory() {
		Vector vCounter = new Vector();
		Enumeration enumKeys = ExtendedPropertiesBundle.cache.keys();
		int iSize = 0;
		while (enumKeys.hasMoreElements()) {
			Object oKey = enumKeys.nextElement();
			ResourceBundle ress = (ResourceBundle) ExtendedPropertiesBundle.cache.get(oKey);
			if (ress instanceof ExtendedPropertiesBundle) {
				for (int j = 0; j < ((ExtendedPropertiesBundle) ress).getBundles().length; j++) {
					ResourceBundle resourceBundle = ((ExtendedPropertiesBundle) ress).getBundles()[j];
					if (resourceBundle == null) {
						continue;
					}
					boolean counted = false;
					for (int i = 0; i < vCounter.size(); i++) {
						if (resourceBundle == vCounter.get(i)) {
							counted = true;
						}
					}
					if (counted == false) {
						Enumeration keys = resourceBundle.getKeys();
						while (keys.hasMoreElements()) {
							Object key = keys.nextElement();
							String sValue = resourceBundle.getString(key.toString());
							if (sValue == null) {
								sValue = "";
							}
							iSize = iSize + (key.toString().length() * 2) + (sValue.length() * 2);
						}
						vCounter.add(resourceBundle);
					}
				}
			} else {
				boolean counted = false;
				for (int i = 0; i < vCounter.size(); i++) {
					if (ress == vCounter.get(i)) {
						counted = true;
					}
				}
				if (counted == false) {
					Enumeration keys = ress.getKeys();
					while (keys.hasMoreElements()) {
						Object key = keys.nextElement();
						String sValue = ress.getString(key.toString());
						if (sValue == null) {
							sValue = "";
						}
						iSize = iSize + (key.toString().length() * 2) + (sValue.length() * 2);
					}
					vCounter.add(ress);
				}
			}
		}
		return iSize;
	}

	protected static UtilReferenceLocator	locator;

	protected static String					dbBundleManagerName;

	protected static boolean				useDatabaseBundle	= false;

	public static void useDatabaseBundle(UtilReferenceLocator locator, String databaseBundleManagerName) {
		ExtendedPropertiesBundle.locator = locator;
		ExtendedPropertiesBundle.dbBundleManagerName = databaseBundleManagerName;

		ExtendedPropertiesBundle.databaseCache = new Hashtable();
		ExtendedPropertiesBundle.extendedBundleCache = new Hashtable();
		ExtendedPropertiesBundle.useDatabaseBundle = true;

		if (ExtendedPropertiesBundle.cache != null) {
			// Include the database bundle if exist in the existing resources
			Enumeration keys = ExtendedPropertiesBundle.cache.keys();
			while (keys.hasMoreElements()) {
				CacheKey key = (CacheKey) keys.nextElement();
				Hashtable allDatabaseResourceBundles = ExtendedPropertiesBundle.getAllDatabaseResourceBundles(key.locale);

				if (allDatabaseResourceBundles != null) {
					ExtendedPropertiesBundle.databaseCache.putAll(allDatabaseResourceBundles);
				}

				ExtendedPropertiesBundle propBundle = (ExtendedPropertiesBundle) ExtendedPropertiesBundle.cache.remove(key);
				Vector resourceBundleList = ExtendedPropertiesBundle.getBundles(key.baseName, key.locale);
				propBundle.setBundles(resourceBundleList);
				ExtendedPropertiesBundle.cache.put(key, propBundle);
			}
		}
	}

	// Obtener el primer locale para el init
	public static Locale getDefaultCacheLocale() {
		if (ExtendedPropertiesBundle.cache != null) {
			Enumeration keys = ExtendedPropertiesBundle.cache.keys();
			while (keys.hasMoreElements()) {
				CacheKey key = (CacheKey) keys.nextElement();
				return key.locale;
			}
		}
		return null;
	}

	protected static ResourceBundle getDatabaseBundle(String resourcesFileName, Locale locale) {
		try {
			if (ExtendedPropertiesBundle.useDatabaseBundle && (ExtendedPropertiesBundle.locator != null) && (((EntityReferenceLocator) ExtendedPropertiesBundle.locator)
					.getSessionId() >= 0)) {
				IDatabaseBundleManager remoteReference = (IDatabaseBundleManager) ExtendedPropertiesBundle.locator.getRemoteReference(ExtendedPropertiesBundle.dbBundleManagerName,
						((EntityReferenceLocator) ExtendedPropertiesBundle.locator).getSessionId());
				ResourceBundle bundle = remoteReference.getBundle(resourcesFileName, locale, ((EntityReferenceLocator) ExtendedPropertiesBundle.locator).getSessionId());
				if (bundle != null) {
					return bundle;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected static Hashtable getAllDatabaseResourceBundles(Locale locale) {
		try {
			if (ExtendedPropertiesBundle.useDatabaseBundle && (ExtendedPropertiesBundle.locator != null) && (((EntityReferenceLocator) ExtendedPropertiesBundle.locator)
					.getSessionId() >= 0)) {

				Hashtable bundles = null;
				if ((((ClientReferenceLocator) ExtendedPropertiesBundle.locator).getInitialContext() != null) && ((ClientReferenceLocator) ExtendedPropertiesBundle.locator)
						.getInitialContext().containsKey(InitialContext.ALL_RESOURCES_BUNDLES)) {
					bundles = (Hashtable) ((ClientReferenceLocator) ExtendedPropertiesBundle.locator).getInitialContext().get(InitialContext.ALL_RESOURCES_BUNDLES);
				} else {
					IDatabaseBundleManager remoteReference = (IDatabaseBundleManager) ExtendedPropertiesBundle.locator.getRemoteReference(
							ExtendedPropertiesBundle.dbBundleManagerName, ((EntityReferenceLocator) ExtendedPropertiesBundle.locator).getSessionId());
					bundles = remoteReference.getAllResourceBundles(locale, ((EntityReferenceLocator) ExtendedPropertiesBundle.locator).getSessionId());
				}

				if ((bundles != null) && !bundles.isEmpty()) {
					Enumeration keys = bundles.keys();
					Hashtable result = new Hashtable();
					while (keys.hasMoreElements()) {
						String baseName = (String) keys.nextElement();
						result.put(new CacheKey(baseName, locale), bundles.get(baseName));
					}
					return result;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void addModuleResourceBundle(String resourceBundle) {
		if ((resourceBundle == null) || (resourceBundle.length() == 0)) {
			return;
		}

		StringTokenizer st = new StringTokenizer(resourceBundle, ";");
		while (st.hasMoreTokens()) {
			String bundle = st.nextToken();
			if (!ExtendedPropertiesBundle.moduleResources.contains(bundle)) {
				ExtendedPropertiesBundle.moduleResources.add(bundle);
				// Check if exits bundles already cache.
				Enumeration<CacheKey> keys = ExtendedPropertiesBundle.cache.keys();
				while (keys.hasMoreElements()) {
					CacheKey key = keys.nextElement();
					ExtendedPropertiesBundle extendedPropertiesBundle = (ExtendedPropertiesBundle) ExtendedPropertiesBundle.cache.get(key);
					ResourceBundle bundleToAdd = ResourceBundle.getBundle(bundle, key.getLocale());
					extendedPropertiesBundle.addBundle(bundleToAdd);
					ExtendedPropertiesBundle.logger.debug("{} {} has been added to {}", bundle, key.getLocale(), key);
				}
			} else {
				ExtendedPropertiesBundle.logger.warn("The {} bundle already exists in module resource list");
			}
		}
	}

	public static void reloadBundle() {
		if (ExtendedPropertiesBundle.cache != null) {
			ExtendedPropertiesBundle.cache.clear();
		}
		if (ExtendedPropertiesBundle.useDatabaseBundle) {
			ExtendedPropertiesBundle.databaseCache.clear();
		}

		if (ExtendedPropertiesBundle.extendedBundleCache != null) {
			ExtendedPropertiesBundle.extendedBundleCache.clear();
		}

		if ((((ClientReferenceLocator) ExtendedPropertiesBundle.locator).getInitialContext() != null) && ((ClientReferenceLocator) ExtendedPropertiesBundle.locator)
				.getInitialContext().containsKey(InitialContext.ALL_RESOURCES_BUNDLES)) {
			((ClientReferenceLocator) ExtendedPropertiesBundle.locator).getInitialContext().remove(InitialContext.ALL_RESOURCES_BUNDLES);
		}

		ResourceBundle extendedBundle = ExtendedPropertiesBundle.getExtendedBundle(((MainApplication) ApplicationManager.getApplication()).getResourcesFileName(),
				ApplicationManager.getApplication().getResourceBundle().getLocale());
		ApplicationManager.getApplication().setResourceBundle(extendedBundle);
	}

	public static boolean isUsingDatabaseBundle() {
		return ExtendedPropertiesBundle.useDatabaseBundle;
	}

	public static String getDbBundleManagerName() {
		return ExtendedPropertiesBundle.dbBundleManagerName;
	}

}