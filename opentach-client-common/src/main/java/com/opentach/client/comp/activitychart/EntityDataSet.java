package com.opentach.client.comp.activitychart;

import com.ontimize.db.EntityResult;

public class EntityDataSet {
	String			entity;
	Task			task;
	ChartDataRender	render;
	String			taskName;
	boolean			visible;
	EntityResult	data;

	public EntityDataSet(String entity, String taskName) {
		this.entity = entity;
		this.taskName = taskName;

		try {
			Class<Task> cls = (Class<Task>) Class.forName(taskName);
			this.task = cls.newInstance();
			if (this.task instanceof ChartDataRender) {
				this.render = ((ChartDataRenderProvider) this.task).getChartDataRender();
			}
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		this.visible = true;
	}

	public String getEntity() {
		return this.entity;
	}

	public void setData(EntityResult data) {
		this.data = data;
	}

	public EntityResult getData() {
		return this.data;
	}

	public Task getnewTaskInstance() {
		try {
			Class<Task> cls = (Class<Task>) Class.forName(this.taskName);
			return cls.newInstance();
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean isVisible() {
		return this.visible;
	}

	public void setVisible(boolean v) {
		this.visible = v;
	}

	public void setChartDataRender(ChartDataRender r) {
		this.render = r;
	}

	public ChartDataRender getChartDataRender() {
		return this.render;
	}

	public String getTaskName() {
		return this.taskName;
	}

}