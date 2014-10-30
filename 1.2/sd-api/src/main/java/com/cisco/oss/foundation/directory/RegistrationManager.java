/**
 * Copyright (c) 2013-2014 by Cisco Systems, Inc. 
 * All rights reserved. 
 */
package com.cisco.oss.foundation.directory;

import java.util.List;

import com.cisco.oss.foundation.directory.entity.OperationalStatus;
import com.cisco.oss.foundation.directory.entity.Permission;
import com.cisco.oss.foundation.directory.entity.ProvidedServiceInstance;
import com.cisco.oss.foundation.directory.entity.User;
import com.cisco.oss.foundation.directory.exception.ServiceException;

/**
 * The service registration lifecycle management interface.
 * 
 * This interface is intended for the service provider to register/update/unregister a ProvidedServiceInstance.
 * 
 * @author zuxiang
 *
 */
public interface RegistrationManager {

	/**
	 * Register a new ProvidedServiceInstance.
	 * 
	 * Register a new ProvidedServiceInstance to Service Directory.
	 * 
	 * @param serviceInstance	The ProvidedServiceInstance.
	 * @throws ServiceException
	 */
	public void registerService(ProvidedServiceInstance serviceInstance) throws ServiceException;
	
	/**
	 * Register a new ProviderServiceInstance with ServiceInstanceHealth.
	 * 
	 * It registers a new ProviderServiceInstance and attaches a ServiceInstanceHealth callback. 
	 * Directory server will invoke ServiceInstanceHealth periodically to update the OperationalStatus of the ProviderServiceInstance on behalf of 
	 * the Service Provider.
	 * 
	 * @param serviceInstance	The ProvidedServiceInstance.
	 * @param registryHealth		The ServiceInstanceHealth.
	 * @throws ServiceException
	 */
	public void registerService(ProvidedServiceInstance serviceInstance, ServiceInstanceHealth registryHealth) throws ServiceException;
	
	/**
	 * Update the OperationalStatus of the ProvidedServiceInstance.
	 * 
	 * It is a convenient method to update the OperationalStatus of the ProvidedServiceInstance.
	 * 
	 * @param serviceName	The name of the service.
	 * @param providerId	The providerId of the ProvidedServiceInstance.
	 * @param status		The OperationalStatus of the ProvidedServiceInstance.
	 * @throws ServiceException
	 */
	public void updateServiceOperationalStatus(String serviceName, String providerId, OperationalStatus status) throws ServiceException;
	
	/**
	 * Update the URI of the ProvidedServiceInstance.
	 * 
	 * It is a convenient method to update the URI of the ProvidedServiceInstance.
	 * 
	 * @param serviceName	The name of the service.
	 * @param providerId	The providerId of the ProvidedServiceInstance.
	 * @param uri		The URI of the ProvidedServiceInstance.
	 * @throws ServiceException
	 */
	public void updateServiceUri(String serviceName, String providerId, String uri) throws ServiceException;
	
	
	/**
	 * Update the ProvidedServiceInstance.
	 * 
	 * Update the existing ProvidedServiceInstance.
	 * For the referenced metadata Map in the ProvidedServiceInstance, it will not update it when it is null. 
	 * 
	 * @param serviceInstance	The ProvidedServiceInstance.
	 * @throws ServiceException
	 */
	public void updateService(ProvidedServiceInstance serviceInstance) throws ServiceException;
	
	/**
	 * Unregister the ProvidedServiceInstance.
	 * 
	 * Unregister the existing ProvidedServiceInstance in the directory server.
	 * 
	 * @param serviceName	The name of the Service.
	 * @param providerId	The providerId of ProvidedServiceInstance.
	 * @throws ServiceException
	 */
	public void unregisterService(String serviceName, String providerId) throws ServiceException;
	
	/**
	 * Create a new User.
	 * 
	 * @param user
	 * 		the User.
	 * @throws ServiceException
	 */
	public void createUser(User user, String password) throws ServiceException;
	
	/**
	 * Get a User by the name.
	 * 
	 * @param name
	 * 		the name of the User.
	 * @return
	 * 		the User.
	 * @throws ServiceException
	 */
	public User getUser(String name) throws ServiceException;
	
	/**
	 * Get all Users.
	 * 
	 * @return
	 * 		all users.
	 * @throws ServiceException
	 */
	public List<User> getAllUsers() throws ServiceException;
	
	/**
	 * Update the User.
	 * 
	 * @param user
	 * 		the User.
	 * @throws ServiceException
	 */
	public void updateUser(User user) throws ServiceException;
	
	/**
	 * Delete a User by name.
	 * 
	 * @param name
	 * 		the name of the User.
	 * @throws ServiceException
	 */
	public void deleteUser(String name) throws ServiceException;
	
	/**
	 * Set the permission list for the user.
	 * 
	 * If the permissions is null or empty, it equals to set the User Permission to NONE.
	 * The Permission should have a corresponding User, and delete the User will delete the corresponding Permission.
	 * 
	 * @param userName
	 * 		the user name.
	 * @param permissions
	 * 		the permission list of the user.
	 * @throws ServiceException
	 */
	public void setUserPermission(String userName, List<Permission> permissions) throws ServiceException;
	
	/**
	 * Set the password for the User.
	 * 
	 * @param userName
	 * 		the name of the User.
	 * @param password
	 * 		the password String.
	 * @throws ServiceException
	 */
	public void setUserPassword(String userName, String password) throws ServiceException;
}