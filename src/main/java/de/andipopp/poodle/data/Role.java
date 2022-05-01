package de.andipopp.poodle.data;

import com.vaadin.flow.component.checkbox.CheckboxGroup;

import de.andipopp.poodle.data.entity.User;

public enum Role {
    USER, ADMIN;
	
	public static class UserRoleCheckboxGroup extends CheckboxGroup<Role> {
		
		User user;
		
		/**
		 * @param user
		 */
		public UserRoleCheckboxGroup(User user) {
			this.user = user;
			this.setItems(USER, ADMIN);
			for(Role role : user.getRoles()) {
				this.select(role);
			}
			this.addValueChangeListener(e -> updateRoles());
		}

		private void updateRoles() {
			user.getRoles().clear();
			user.getRoles().addAll(getSelectedItems());
		}
	}
}
