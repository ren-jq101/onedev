package io.onedev.server.web.page.project.blob.render.renderers.nocommits;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;

import io.onedev.server.event.RefUpdated;
import io.onedev.server.util.SecurityUtils;
import io.onedev.server.web.component.floating.FloatingPanel;
import io.onedev.server.web.component.link.DropdownLink;
import io.onedev.server.web.component.link.ViewStateAwareAjaxLink;
import io.onedev.server.web.component.menu.MenuItem;
import io.onedev.server.web.component.menu.MenuLink;
import io.onedev.server.web.component.modal.ModalLink;
import io.onedev.server.web.component.modal.ModalPanel;
import io.onedev.server.web.page.project.blob.BlobUploadPanel;
import io.onedev.server.web.page.project.blob.render.BlobRenderContext;
import io.onedev.server.web.page.project.blob.render.BlobRenderContext.Mode;

@SuppressWarnings("serial")
public class NoCommitsPanel extends Panel {

	private final BlobRenderContext context;
	
	public NoCommitsPanel(String id, BlobRenderContext context) {
		super(id);
		this.context = context;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		if (SecurityUtils.canWriteCode(context.getProject())) {
			add(new MenuLink("addFiles") {

				@Override
				protected List<MenuItem> getMenuItems(FloatingPanel dropdown) {
					List<MenuItem> menuItems = new ArrayList<>();
					menuItems.add(new MenuItem() {

						@Override
						public String getLabel() {
							return "Create New File";
						}

						@Override
						public WebMarkupContainer newLink(String id) {
							return new ViewStateAwareAjaxLink<Void>(id) {

								@Override
								public void onClick(AjaxRequestTarget target) {
									context.onModeChange(target, Mode.ADD, null);
									dropdown.close();
								}
								
							};
						}
						
					});
					
					menuItems.add(new MenuItem() {

						@Override
						public String getLabel() {
							return "Upload Files";
						}

						@Override
						public WebMarkupContainer newLink(String id) {
							return new ModalLink(id) {

								@Override
								public void onClick(AjaxRequestTarget target) {
									super.onClick(target);
									dropdown.close();
								}

								@Override
								protected Component newContent(String id, ModalPanel modal) {
									return new BlobUploadPanel(id, context) {

										@Override
										public void onCancel(AjaxRequestTarget target) {
											modal.close();
										}

										@Override
										public void onCommitted(AjaxRequestTarget target, RefUpdated refUpdated) {
											context.onCommitted(target, refUpdated, null);
											modal.close();
										}
										
									};
								}
								
							};
						}
						
					});
					return menuItems;
				}
				
			});		
		} else {
			add(new WebMarkupContainer("addFiles") {

				@Override
				protected void onComponentTag(ComponentTag tag) {
					super.onComponentTag(tag);
					tag.setName("span");
				}
				
			});
		}

		add(new DropdownLink("pushInstructions") {

			@Override
			protected Component newContent(String id, FloatingPanel dropdown) {
				Fragment fragment = new Fragment(id, "pushInstructionsFrag", NoCommitsPanel.this);
				fragment.add(new Label("url1", context.getProject().getUrl()));
				fragment.add(new Label("url2", context.getProject().getUrl()));
				return fragment;
			}
			
		});
		setOutputMarkupId(true);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(new NoCommitsCssResourceReference()));
	}

}