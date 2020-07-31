package com.opentach.client.mailmanager.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.opentach.common.mailmanager.dto.MailFolder;

/**
 * The Class DocumentationTreeModel.
 */
public class MailFolderTreeModel implements TreeModel {

	/** The Constant serialVersionUID. */
	private static final long	serialVersionUID	= 1L;
	/** Listeners. */
	protected EventListenerList	listenerList		= new EventListenerList();

	/** The category root. */
	private MailFolder			uncategorized;

	/** The root categories. */
	private List<MailFolder>	rootCategories;

	// /** The tree root. */
	private final Object		treeRoot			= "ROOT_NODE";

	/** The all files. */
	private final String		allFiles			= "mmng.all_mails";

	/**
	 * Instantiates a new documentation tree model.
	 */
	public MailFolderTreeModel() {
		super();
		this.setCategoryTree(new MailFolder(null, null, "/", null, null));
	}

	/**
	 * Sets the category tree.
	 *
	 * @param category
	 *            the new category tree
	 * @param idDocument
	 */
	public void setCategoryTree(MailFolder category) {
		this.uncategorized = category;
		if (this.uncategorized == null) {
			this.uncategorized = new MailFolder(null, null, "/", null, null);
		}
		this.rootCategories = this.uncategorized.getChildren();
		if (this.rootCategories == null) {
			this.rootCategories = new ArrayList<>();
		}
		this.uncategorized.setChildren(new ArrayList<MailFolder>());
		this.fireTreeStructureChanged(this, new TreePath(this.allFiles));
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#getRoot()
	 */
	@Override
	public Object getRoot() {
		return this.treeRoot;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
	 */
	@Override
	public Object getChild(Object parent, int index) {
		if (this.treeRoot.equals(parent)) {
			return this.allFiles;
		} else if (this.allFiles.equals(parent)) {
			if (index == 0) {
				return this.uncategorized;
			} else if ((index >= 1) && ((index - 1) < this.rootCategories.size())) {
				return this.rootCategories.get(index - 1);
			}
		} else if (this.uncategorized.equals(parent)) {
			return null;
		} else if (parent instanceof MailFolder) {
			return ((MailFolder) parent).getChildren().get(index);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
	 */
	@Override
	public int getChildCount(Object parent) {
		if (this.treeRoot.equals(parent)) {
			return 1;
		} else if (this.allFiles.equals(parent)) {
			return 1 + this.rootCategories.size();
		} else if (this.uncategorized.equals(parent)) {
			return 0;
		} else if (parent instanceof MailFolder) {
			return ((MailFolder) parent).getChildren().size();
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
	 */
	@Override
	public boolean isLeaf(Object node) {
		if (node instanceof MailFolder) {
			return ((MailFolder) node).getChildren().isEmpty();
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if (this.treeRoot.equals(parent)) {
			return 0;
		} else if (this.allFiles.equals(parent)) {
			if (this.uncategorized.equals(child)) {
				return 0;
			}
			return 1 + this.rootCategories.indexOf(child);
		} else if (this.uncategorized.equals(parent)) {
			return 0;
		} else if (parent instanceof MailFolder) {
			return ((MailFolder) parent).getChildren().indexOf(child);
		}
		return 0;
	}

	/**
	 * Adds a listener for the TreeModelEvent posted after the tree changes.
	 *
	 * @param l
	 *            the listener to add
	 * @see #removeTreeModelListener
	 */
	@Override
	public void addTreeModelListener(TreeModelListener l) {
		this.listenerList.add(TreeModelListener.class, l);
	}

	/**
	 * Removes a listener previously added with <B>addTreeModelListener()</B>.
	 *
	 * @param l
	 *            the listener to remove
	 * @see #addTreeModelListener
	 */
	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		this.listenerList.remove(TreeModelListener.class, l);
	}

	/**
	 * Notifies all listeners that have registered interest for notification on this event type. The event instance is lazily created using the parameters passed into the fire
	 * method.
	 *
	 * @param source
	 *            the source of the {@code TreeModelEvent}; typically {@code this}
	 * @param path
	 *            the path to the parent of the structure that has changed; use {@code null} to identify the root has changed
	 */
	private void fireTreeStructureChanged(Object source, TreePath path) {
		// Guaranteed to return a non-null array
		Object[] listeners = this.listenerList.getListenerList();
		TreeModelEvent mevent = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				// Lazily create the event:
				if (mevent == null) {
					mevent = new TreeModelEvent(source, path);
				}
				((TreeModelListener) listeners[i + 1]).treeStructureChanged(mevent);
			}
		}
	}

	/**
	 * Notifies all listeners that have registered interest for notification on this event type. The event instance is lazily created using the parameters passed into the fire
	 * method.
	 *
	 * @param source
	 *            the source of the {@code TreeModelEvent}; typically {@code this}
	 * @param path
	 *            the path to the parent the nodes were added to
	 * @param childIndices
	 *            the indices of the new elements
	 * @param children
	 *            the new elements
	 */
	protected void fireTreeNodesInserted(Object source, Object[] path, int[] childIndices, Object[] children) {
		// Guaranteed to return a non-null array
		Object[] listeners = this.listenerList.getListenerList();
		TreeModelEvent mevent = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				// Lazily create the event:
				if (mevent == null) {
					mevent = new TreeModelEvent(source, path, childIndices, children);
				}
				((TreeModelListener) listeners[i + 1]).treeNodesInserted(mevent);
			}
		}
	}

	/**
	 * Notifies all listeners that have registered interest for notification on this event type. The event instance is lazily created using the parameters passed into the fire
	 * method.
	 *
	 * @param source
	 *            the source of the {@code TreeModelEvent}; typically {@code this}
	 * @param path
	 *            the path to the parent the nodes were removed from
	 * @param childIndices
	 *            the indices of the removed elements
	 * @param children
	 *            the removed elements
	 */
	protected void fireTreeNodesRemoved(Object source, Object[] path, int[] childIndices, Object[] children) {
		// Guaranteed to return a non-null array
		Object[] listeners = this.listenerList.getListenerList();
		TreeModelEvent mevent = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				// Lazily create the event:
				if (mevent == null) {
					mevent = new TreeModelEvent(source, path, childIndices, children);
				}
				((TreeModelListener) listeners[i + 1]).treeNodesRemoved(mevent);
			}
		}
	}

	public void insertCategory(MailFolder parentCategory, MailFolder newCategory) {
		if (parentCategory == null) {
			this.rootCategories.add(newCategory);
			this.fireTreeNodesInserted(this, new Object[] { this.getRoot(), this.allFiles }, new int[] { (1 + this.rootCategories.size()) - 1 }, new Object[] { newCategory });
		} else {
			parentCategory.addChild(newCategory);
			List<Object> path = new ArrayList<>();
			MailFolder tmp = newCategory.getParent();
			do {
				path.add(tmp);
				tmp = tmp.getParent();
			} while ((tmp != null) && (tmp.getMfdId() != null));
			path.add(this.allFiles);
			path.add(this.getRoot());
			Collections.reverse(path);

			this.fireTreeNodesInserted(this, path.toArray(), new int[] { parentCategory.getChildren().size() - 1 }, new Object[] { newCategory });
		}
	}

	public List<MailFolder> getRootCategories() {
		return this.rootCategories;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath, java.lang.Object)
	 */
	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		// do nothing
	}
}
