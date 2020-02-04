package com.deepoove.swagger.diff.compare;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.deepoove.swagger.diff.model.ElProperty;

import io.swagger.models.Model;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;

/**
 * compare two model
 * @author Sayi
 * @version
 */
public class ModelDiff {

	private List<ElProperty> increased;
	private List<ElProperty> missing;
	private List<ElProperty> changed;

	Map<String, Model> oldDedinitions;
	Map<String, Model> newDedinitions;

	private ModelDiff() {
		increased = new ArrayList<>();
		missing = new ArrayList<>();
		changed = new ArrayList<>();
	}

	public static ModelDiff buildWithDefinition(final Map<String, Model> left,
			final Map<String, Model> right) {
		ModelDiff diff = new ModelDiff();
		diff.oldDedinitions = left;
		diff.newDedinitions = right;
		return diff;
	}

	public ModelDiff diff(final Model leftModel, final Model rightModel) {
		return this.diff(leftModel, rightModel, null, new HashSet<Model>());
	}

	public ModelDiff diff(final Model leftModel, final Model rightModel, final String parentEl) {
		return this.diff(leftModel, rightModel, parentEl, new HashSet<Model>());
	}

	private ModelDiff diff(final Model leftModel, final Model rightModel, final String parentEl, final Set<Model> visited) {
		// Stop recursing if both models are null
		// OR either model is already contained in the visiting history
		if ((null == leftModel && null == rightModel) || visited.contains(leftModel) || visited.contains(rightModel)) {
			return this;
		}
		Map<String, Property> leftProperties = null == leftModel ? null : leftModel.getProperties();
		Map<String, Property> rightProperties = null == rightModel ? null : rightModel.getProperties();

		// Diff the properties
		MapKeyDiff<String, Property> propertyDiff = MapKeyDiff.diff(leftProperties, rightProperties);

		increased.addAll(convert2ElPropertys(propertyDiff.getIncreased(), parentEl));
		missing.addAll(convert2ElPropertys(propertyDiff.getMissing(), parentEl));

		// Recursively find the diff between properties
		List<String> sharedKey = propertyDiff.getSharedKey();
		sharedKey.stream().forEach((key) -> {
			Property left = leftProperties.get(key);
			Property right = rightProperties.get(key);

			if ((left instanceof RefProperty) && (right instanceof RefProperty)) {
				String leftRef = ((RefProperty) left).getSimpleRef();
				String rightRef = ((RefProperty) right).getSimpleRef();

				diff(oldDedinitions.get(leftRef), newDedinitions.get(rightRef),
						buildElString(parentEl, key),
						copyAndAdd(visited, leftModel, rightModel));

			} else if (left != null && right != null && !left.equals(right)) {
				// Add a changed ElProperty if not a Reference
			    // Useless
				changed.add(convert2ElProperty(key, parentEl, left, right));
			}
		});
		return this;
	}

	private Collection<? extends ElProperty> convert2ElPropertys(
			final Map<String, Property> propMap, final String parentEl) {

		List<ElProperty> result = new ArrayList<>();
		if (null == propMap) {
			return result;
		}

		for (Entry<String, Property> entry : propMap.entrySet()) {
		    // TODO Recursively get the properties
			result.add(convert2ElProperty(entry.getKey(), parentEl, entry.getValue(), null));
		}
		return result;
	}

	private String buildElString(final String parentEl, final String propName) {
		return null == parentEl ? propName : (parentEl + "." + propName);
	}


	private ElProperty convert2ElProperty(final String propName, final String parentEl, final Property property, final Property right) {
		ElProperty pWithPath = new ElProperty();
		pWithPath.setProperty(property);
		pWithPath.setRightProperty(right);
		String buildElString = buildElString(parentEl, propName);
		pWithPath.setEl(buildElString);
		return pWithPath;
	}

	@SuppressWarnings("unchecked")
    private <T> Set<T> copyAndAdd(final Set<T> set, final T... add) {
		Set<T> newSet = new HashSet<>(set);
		newSet.addAll(Arrays.asList(add));
		return newSet;
	}

	public List<ElProperty> getIncreased() {
		return increased;
	}

	public void setIncreased(final List<ElProperty> increased) {
		this.increased = increased;
	}

	public List<ElProperty> getMissing() {
		return missing;
	}

	public void setMissing(final List<ElProperty> missing) {
		this.missing = missing;
	}

	public List<ElProperty> getChanged() {
		return changed;
	}

	public void setChanged(final List<ElProperty> changed) {
		this.changed = changed;
	}
}
