package com.zj.test.client;

import java.util.List;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.Server.Status;
import com.sinosoft.openstack.CloudManipulator;
import com.sinosoft.openstack.CloudManipulatorFactory;
import com.sinosoft.openstack.type.CloudConfig;

public class ClientTest001 {
	public static void main(String[] args) {
		CloudConfig appConfig=new CloudConfig();
		String projectId="6cc1b9378d8044529e1251b8ab34fe81";
		
		appConfig.setCloudManipulatorVersion("v3");
		appConfig.setAuthUrl("http://192.168.100.81:5000/v3");
		appConfig.setAdminUsername("admin");
		appConfig.setAdminPassword("123456");
		appConfig.setAdminProjectName("admin");
		appConfig.setPublicNetworkId("5bdaa577-f652-4b41-a666-11d9654fb4d6");
		appConfig.setAdminUserId("f63cdb5acf474cc1a5fd4631eebae625");
		appConfig.setDomainName("default");
		appConfig.setDomainId("27c82671c4bb4d61aab9e46281331c3a");
		appConfig.setAdminProjectId("6cc1b9378d8044529e1251b8ab34fe81");
		appConfig.setAdminRoleName("414fd8e07ba64aacbb7e24c746eae0ba");
		appConfig.setAodhServiceUrl("http://192.168.100.201:8042/v2/alarms");
		//appConfig.setAlarmThresholdRulePeriod(Integer.parseInt("600"));
		
		CloudManipulator cloud = CloudManipulatorFactory.createCloudManipulator(appConfig, projectId);
		
		List<? extends Server> serverList=cloud.getServers();
		for (Server server : serverList) {
			//System.out.println(server.getInstanceName());
			Status serverStatus=server.getStatus();
			System.out.println(server.getName()+":"+serverStatus);
			if (serverStatus.toString().equals("SHUTOFF")) {
				cloud.startServer(server.getId());
			}else if (serverStatus.toString().equals("ACTIVE")) {
				cloud.stopServer(server.getId());
			}
			
		}
		
	}
	
	

}
