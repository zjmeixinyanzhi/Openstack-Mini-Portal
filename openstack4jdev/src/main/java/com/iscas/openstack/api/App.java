package com.iscas.openstack.api;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import org.openstack4j.api.OSClient;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.model.compute.Action;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.identity.v3.Project;
import org.openstack4j.model.identity.v3.User;
import org.openstack4j.model.identity.v3.Tenant;
import org.openstack4j.model.image.Image;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.Router;
import org.openstack4j.model.network.Subnet;
import org.openstack4j.openstack.OSFactory;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )   {
    
    	Identifier domainIdentifier = Identifier.byId("27c82671c4bb4d61aab9e46281331c3a");
    	Identifier domainIdentifier2 = Identifier.byName("default");
    	
//    	@SuppressWarnings("rawtypes")
//		OSClient os = OSFactory.builderV3()
//    	            .endpoint("http://192.168.100.201:5000/v3")
//    	            .credentials("f63cdb5acf474cc1a5fd4631eebae625", "123456")
//    	            .authenticate();
    	
    	//unscoped authentication
    	//as the username is not unique across domains you need to provide the domainIdentifier
    	OSClientV3 os = OSFactory.builderV3()
    	                       .endpoint("http://192.168.100.201:5000/v3")
    	                       .credentials("admin","123456", domainIdentifier)
    	                       .authenticate();
//    	# ERROR
//    	# domain scoped authentication
//    	# using the unique userId does not require a domainIdentifier
//    	OSClientV3 os2 = OSFactory.builderV3()
//    	                    .endpoint("http://192.168.100.201:5000/v3")
//    	                    .credentials("f63cdb5acf474cc1a5fd4631eebae625", "123456")
//    	                    .scopeToDomain(Identifier.byId("27c82671c4bb4d61aab9e46281331c3a"))
//    	                    .authenticate();
    	
    	
    	//# Scoping to a project just by name isn't possible as the project name is only unique within a domain. 
    	//# You can either use this as the id of the project is unique across domains  			
    	OSClientV3 os3 = OSFactory.builderV3()
    	                    .endpoint("http://192.168.100.201:5000/v3")
    	                    .credentials("f63cdb5acf474cc1a5fd4631eebae625", "123456")
    	                    .scopeToProject(Identifier.byName("admin"), Identifier.byName("default"))
    	                    .authenticate();
//    	# ERROR
    	//    	# Or alternatively
//    	OSClientV3 os4 = OSFactory.builderV3()
//    	                    .endpoint("http://192.168.100.201:5000/v3")
//    	                    .credentials("f63cdb5acf474cc1a5fd4631eebae625", "123456")
//    	                    .scopeToDomain(Identifier.byName("default"))
//    	                    .authenticate();    	
    	
        System.out.println( ">>"+os.getEndpoint());
//        System.out.println( ">>"+os4.imagesV2().list().size());
//        System.out.println( ">>"+os4.blockStorage().getLimits());
        
     // Find all Users
        List<? extends User> users = os.identity().users().list();
        for (Iterator iterator = users.iterator(); iterator.hasNext();) {
			User user = (User) iterator.next();
			System.out.println("Name:"+user.getName()+" PW:"+user.getPassword()+" ID:"+user.getId());
		}

        // List all Tenants
        List<? extends Project> projects =  os.identity().projects().list();
        for (Iterator iterator = projects.iterator(); iterator.hasNext();) {
			Project project = (Project) iterator.next();
			System.out.println(" Name:"+project.getName()+" ID:"+project.getDomainId());
		}

        // Find all Compute Flavors
        List<? extends Flavor> flavors = os.compute().flavors().list();
        for (Iterator iterator = flavors.iterator(); iterator.hasNext();) {
			Flavor flavor = (Flavor) iterator.next();
			System.out.println("Name:"+flavor.getName()+" Quotes:"+flavor.getVcpus()
					+"-"+flavor.getRam()+"-"+flavor.getDisk());
		}

        // Find all running Servers
        List<? extends Server> servers = os.compute().servers().list();
        for (Iterator iterator = servers.iterator(); iterator.hasNext();) {
			Server server = (Server) iterator.next();
			System.out.println(" Name:"+server.getName()+" ID:"+server.getId()+" Image:"+server.getImageId()
					+" IP:"+server.getAccessIPv4()+"\nPowerStatus:"+server.getPowerState()+" VMStatus:"
					+server.getVmState()+" TaskState:"+server.getTaskState()+" Create Time:"+server.getLaunchedAt());
		}

        // Suspend a Server
        os.compute().servers().action("serverId", Action.SUSPEND);

        // List all Networks
        List<? extends Network> networks = os.networking().network().list();
        for (Iterator iterator = networks.iterator(); iterator.hasNext();) {
			Network network = (Network) iterator.next();
			System.out.println("Name:"+network.getName()+" PhyNet:"+network.getProviderPhyNet()+" Subnet:"+network.getNeutronSubnets() );
		}

        // List all Subnets
        List<? extends Subnet> subnets = os.networking().subnet().list();
        for (Iterator iterator = subnets.iterator(); iterator.hasNext();) {
			Subnet subnet = (Subnet) iterator.next();
			System.out.println(" Name:"+subnet.getName()+" Cidr:"+subnet.getCidr());
		}

        // List all Routers
        List<? extends Router> routers = os.networking().router().list();
        for (Iterator iterator = routers.iterator(); iterator.hasNext();) {
			Router router = (Router) iterator.next();
			System.out.println("Name:"+router.getName()+" Id:"+router.getId()+" Status:"+router.getStatus());
		}

        // List all Images (Glance)
        List<? extends Image> images = os.images().list();
        for (Iterator iterator = images.iterator(); iterator.hasNext();) {
			Image image = (Image) iterator.next();
			System.out.println(" Name:"+image.getName()+" Id:"+image.getId()+" MinDisk:"+image.getMinDisk());
		}

        // Download the Image Data
        InputStream is = os.images().getAsStream("81f07114-e84c-4fda-add8-1a302a33e239");
        OutputStream outputStream =null;
        try {
			outputStream = new FileOutputStream(new File("D:/cirros.raw"));
			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = is.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}

			System.out.println("Done!");
		}  catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (outputStream != null) {
				try {
					// outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    }
}
