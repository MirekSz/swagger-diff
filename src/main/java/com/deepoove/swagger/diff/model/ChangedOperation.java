package com.deepoove.swagger.diff.model;

import java.util.ArrayList;
import java.util.List;

import io.swagger.models.Operation;
import io.swagger.models.parameters.Parameter;

public class ChangedOperation implements Changed {

	private String summary;

	private List<Parameter> addParameters = new ArrayList<>();
	private List<Parameter> missingParameters = new ArrayList<>();

	private List<ChangedParameter> changedParameter = new ArrayList<>();

	private List<ElProperty> addProps = new ArrayList<>();
	private List<ElProperty> missingProps = new ArrayList<>();
	private List<ElProperty> changedProps = new ArrayList<>();

	private Operation operation;

	public List<Parameter> getAddParameters() {
		return addParameters;
	}

	public void setAddParameters(final List<Parameter> addParameters) {
		this.addParameters = addParameters;
	}

	public List<Parameter> getMissingParameters() {
		return missingParameters;
	}

	public void setMissingParameters(final List<Parameter> missingParameters) {
		this.missingParameters = missingParameters;
	}

	public List<ChangedParameter> getChangedParameter() {
		return changedParameter;
	}

	public void setChangedParameter(final List<ChangedParameter> changedParameter) {
		this.changedParameter = changedParameter;
	}

	public List<ElProperty> getAddProps() {
		return addProps;
	}

	public void setAddProps(final List<ElProperty> addProps) {
		this.addProps = addProps;
	}

	public List<ElProperty> getMissingProps() {
		return missingProps;
	}

	public void setMissingProps(final List<ElProperty> missingProps) {
		this.missingProps = missingProps;
	}

	public List<ElProperty> getChangedProps() {
		return changedProps;
	}

	public void setChangedProps(final List<ElProperty> changedProps) {
		this.changedProps = changedProps;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(final String summary) {
		this.summary = summary;
	}

	@Override
	public boolean isDiff() {
		return !addParameters.isEmpty() || !missingParameters.isEmpty()
				|| !changedParameter.isEmpty() || isDiffProp();
	}
	public boolean isDiffProp(){
		return !addProps.isEmpty()
				|| !missingProps.isEmpty()
				|| !changedProps.isEmpty();
	}
	public boolean isDiffParam(){
		return !addParameters.isEmpty() || !missingParameters.isEmpty()
				|| !changedParameter.isEmpty();
	}

	public void setOperation(final Operation operation) {
		this.operation = operation;
	}

	public Operation getOperation() {
		return this.operation;
	}
}
