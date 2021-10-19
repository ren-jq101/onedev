package io.onedev.server.web.editable.parentchoice;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.convert.ConversionException;

import io.onedev.server.OneDev;
import io.onedev.server.entitymanager.ProjectManager;
import io.onedev.server.model.Project;
import io.onedev.server.security.SecurityUtils;
import io.onedev.server.security.permission.CreateChildren;
import io.onedev.server.web.component.stringchoice.StringSingleChoice;
import io.onedev.server.web.editable.PropertyDescriptor;
import io.onedev.server.web.editable.PropertyEditor;

@SuppressWarnings("serial")
public class ParentChoiceEditor extends PropertyEditor<String> {

	private StringSingleChoice input;
	
	public ParentChoiceEditor(String id, PropertyDescriptor propertyDescriptor, 
			IModel<String> propertyModel) {
		super(id, propertyDescriptor, propertyModel);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		Map<String, String> projectPaths = new LinkedHashMap<>();
		for (Project project: OneDev.getInstance(ProjectManager.class).getPermittedProjects(new CreateChildren())) {
			projectPaths.put(project.getPath(), project.getPath());
		}
		
		String selection = getModelObject();
		
    	if (!SecurityUtils.canCreateRootProjects()) {
    		if (projectPaths.size() == 1)
    			selection = projectPaths.entrySet().iterator().next().getKey();
    		else if (!projectPaths.containsKey(selection))
        		selection = null;    		
    	} else if (!projectPaths.containsKey(selection)) {
    		selection = null;
    	}
    	
    	input = new StringSingleChoice("input", Model.of(selection), Model.ofMap(projectPaths), false) {

			@Override
			protected void onInitialize() {
				super.onInitialize();
				getSettings().configurePlaceholder(descriptor);
			}
    		
    	};

    	if (!SecurityUtils.canCreateRootProjects())
    		input.setRequired(true);
    	
        input.setLabel(Model.of(getDescriptor().getDisplayName()));
		input.add(new AjaxFormComponentUpdatingBehavior("change"){

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				onPropertyUpdating(target);
			}
			
		});
		add(input);
	}

	@Override
	protected String convertInputToValue() throws ConversionException {
		return input.getConvertedInput();
	}

}
