/*******************************************************************************
 * Copyright (c) 2013 HealthCare It, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD 3-Clause license
 * which accompanies this distribution, and is available at
 * http://directory.fsf.org/wiki/License:BSD_3Clause
 * 
 * Contributors:
 *     HealthCare It, Inc - initial API and implementation
 ******************************************************************************/
/**
 * 
 */
package com.healthcit.analytics.servlet.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.healthcit.analytics.dao.impl.UserDAO;
import com.healthcit.analytics.model.Role;
import com.healthcit.analytics.model.User;
import com.healthcit.analytics.service.UserManager;
import com.healthcit.analytics.web.model.UserModel;

/**
 * Controller for handling administration requests
 *
 */

@Controller
public class AdminController {
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private UserManager userManager;
	
	@Autowired
	@Qualifier("userValidator")
	private Validator userValidator;
	
	@ModelAttribute("roles")
	public Collection<Role> getRoles(){
		return this.userManager.getRoles();
	}
	
	@RequestMapping(value = "/admin", method=RequestMethod.GET)
	public String getAdminView(Model model){
		model.addAttribute("users", this.userManager.loadUsers());
		return "/admin/admin";
	}
	
	@RequestMapping(value = "/admin/edit_user", method=RequestMethod.GET)
	public String editUserView(@RequestParam("id") Long userId, Model model){
		User user = (User)this.userManager.loadUserById(userId);
		model.addAttribute("user", this.buildUserModel(user));
		return "/admin/user";
	}
	
	@RequestMapping(value="/admin/edit_user", method=RequestMethod.POST)
	public String editUser(@ModelAttribute("user") @Valid UserModel user, BindingResult res, Model model){
		this.userValidator.validate(user, res);
		if(res.hasErrors()){
			model.addAllAttributes(res.getAllErrors());
			return "/admin/user";
		}
		this.userManager.updateUser(this.buildUser(user, this.userManager.getRoles()));
		return "redirect:/admin";
	}
	
	@RequestMapping(value="/admin/add_user", method=RequestMethod.GET)
	public String addUserView(Model model){
		model.addAttribute("user", new UserModel());
		return "/admin/user";
	}
	
	@RequestMapping(value="/admin/add_user", method=RequestMethod.POST)
	public String addUser(@ModelAttribute("user") @Valid UserModel user, BindingResult res, Model model){
		this.userValidator.validate(user, res);
		if(res.hasErrors()){
			model.addAllAttributes(res.getAllErrors());
			return "/admin/user";
		}
		this.userManager.createUser(this.buildUser(user, this.userManager.getRoles()));
		return "redirect:/admin";
	}
	
	private UserModel buildUserModel(User user){
		UserModel userModel =  new UserModel();
		userModel.setId(user.getId());
		userModel.setUsername(user.getUsername());
		userModel.setEmail(user.getEmail());
		ArrayList<Long> roles = new ArrayList<Long>();
		for(GrantedAuthority auth : user.getAuthorities()){
			roles.add(((Role)auth).getId());
		}
		userModel.setRoles(roles);
		return userModel;
	}
	
	private User buildUser(UserModel userModel, Collection<Role> roles){
		User user = new User(userModel.getId(), userModel.getUsername(), StringUtils.isBlank(userModel.getPassword()) ? null : this.passwordEncoder.encodePassword(userModel.getPassword(), null), userModel.getEmail(), null);
		if (userModel.getRoles() != null) {
			for (Long roleId : userModel.getRoles()) {
				for (Role role : roles) {
					if (roleId.equals(role.getId())) {
						user.getAuthorities().add(role);
					}
				}
			}
		}
		return user;
	}
}
