package com.opentach.client.comp.assetchooser;

import java.awt.Window;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.opentach.client.comp.assetchooser.providers.AssetProviderDelegation;
import com.opentach.client.comp.assetchooser.providers.AssetProviderGroup;
import com.opentach.client.comp.assetchooser.providers.AssetProviderResource;
import com.opentach.client.comp.assetchooser.providers.IAssetProvider;
import com.opentach.client.comp.assetchooser.tablewindow.AssetTableWindow;
import com.utilmize.client.gui.field.reference.UReferenceDataField;
import com.utilmize.client.gui.field.reference.window.TableWindow;

public class AssetComboDataField extends UReferenceDataField {

	private static final Logger	logger			= LoggerFactory.getLogger(AssetComboDataField.class);

	public static enum AssetComboMode {
		DRIVER, VEHICLE
	}

	private AssetComboMode			mode;
	private List<IAssetProvider>	assetsProvider;
	private boolean					singleSelectionMode;


	public AssetComboDataField(Hashtable params) throws Exception {
		super(params);
		if (!this.singleSelectionMode) {
			super.setAdvancedQueryMode(true);
		}
	}

	public List<IAssetProvider> getAssetProviders() {
		return this.assetsProvider;
	}

	public IAssetProvider getAssetProvider(String id) {
		return this.getAssetProviders().stream().filter(t -> t.getId().equals(id)).collect(Collectors.toList()).get(0);
	}

	protected void buildAssetProvider() {
		switch (this.mode) {
			case DRIVER:
				this.assetsProvider = Arrays.asList(new AssetProviderResource(this), new AssetProviderGroup(this), new AssetProviderDelegation(this));
				break;
			case VEHICLE:
				this.assetsProvider = Arrays.asList(new AssetProviderResource(this), new AssetProviderGroup(this), new AssetProviderDelegation(this));
				break;
			default:
				this.assetsProvider = Collections.emptyList();
				break;
		}
	}

	@Override
	public void init(Hashtable parameters) {
		this.mode = AssetComboMode.valueOf((String) parameters.get("assetType"));
		switch (this.mode) {
			case DRIVER:
				parameters.putIfAbsent("attr", "IDCONDUCTOR");
				parameters.putIfAbsent("text", "IDCONDUCTOR");
				parameters.putIfAbsent("cod", "IDCONDUCTOR");
				parameters.putIfAbsent("parentkeys", "CG_CONTRATO;CIF");
				parameters.putIfAbsent("entity", "EConductorContFicticio");
				parameters.putIfAbsent("cols", "IDCONDUCTOR;DNI;APELLIDOS;NOMBRE");
				parameters.putIfAbsent("visiblecols", "DNI;APELLIDOS;NOMBRE");
				parameters.putIfAbsent("descriptioncols", "DNI;APELLIDOS;NOMBRE");
				// parameters.putIfAbsent("codsearch", "DNI");
				parameters.putIfAbsent("codvisible", "no");
				// parameters.putIfAbsent("csize", "10");
				break;
			case VEHICLE:
				parameters.putIfAbsent("attr", "MATRICULA");
				parameters.putIfAbsent("text", "MATRICULA");
				parameters.putIfAbsent("cod", "MATRICULA");
				parameters.putIfAbsent("parentkeys", "CG_CONTRATO;CIF");
				parameters.putIfAbsent("entity", "EVehiculoContFicticio");
				parameters.putIfAbsent("cols", "MATRICULA");
				parameters.putIfAbsent("visiblecols", "MATRICULA");
				parameters.putIfAbsent("descriptioncols", "MATRICULA");
				parameters.putIfAbsent("codsearch", "MATRICULA");
				parameters.putIfAbsent("csize", "10");
				// parameters.putIfAbsent("codvisible", "no");
			default:
				break;
		}
		this.singleSelectionMode = ParseUtilsExtended.getBoolean(parameters.get("singleselectionmode"), false);
		this.buildAssetProvider();
		super.init(parameters);
	}

	public AssetComboMode getMode() {
		return this.mode;
	}

	@Override
	public void invalidateCache() {
		super.invalidateCache();
	}

	@Override
	public TableWindow getTableWindow(boolean ensureToLoad) {
		// this.tableWindow = null; // TODO
		return super.getTableWindow(ensureToLoad);
	}


	@Override
	protected TableWindow buildTableWindowInner(Window win) {
		return new AssetTableWindow(this, win);
	}

	@Override
	public Object getValue() {
		// TODO Auto-generated method stub
		return super.getValue();
	}

	@Override
	public void setAdvancedQueryMode(boolean enabled) {
		if (!this.singleSelectionMode) {
			return;
		}
		super.setAdvancedQueryMode(enabled);
	}

}