package arquillilan.tomcat.test;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import arquillilan.tomcat.test.CdiTestBean;


	@RunWith(Arquillian.class)
	public class HibernateTestSample {
		   
		   @Deployment
		   public static WebArchive createTestArchive()
		   {
			   MavenDependencyResolver resolver = DependencyResolvers.use(
					    MavenDependencyResolver.class).loadMetadataFromPom("pom.xml");
			   
			   WebArchive webArchive=  ShrinkWrap
		            .create(WebArchive.class, "ROOT.war")
		            .addClasses(CdiTestBean.class,PersistenceListener.class)
		            .addAsLibraries(
		                   		resolver.artifact("org.jboss.weld.servlet:weld-servlet")
		                   		.artifact("org.apache.tomcat:tomcat-dbcp")
		                   		.artifact("org.hibernate:hibernate-entitymanager")
		                   		.artifact("org.hibernate:hibernate-validator")
		                   		.artifact("org.hibernate:hibernate-core")	
		                   		.artifact("com.h2database:h2")
//		                   		.artifact("mysql:mysql-connector-java")
		                   		
		                   		.resolveAs(GenericArchive.class))
		                    
		            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
		            .addAsWebInfResource("test-persistence.xml", "classes/META-INF/persistence.xml")
		            .addAsManifestResource("context.xml", "context.xml")
		            .setWebXML("hibernate-web.xml");
		      System.out.println(webArchive.toString(true));
		      
		      return webArchive;
		   }

		  @Inject
		  CdiTestBean cdiTestBean;
		  
		  @Inject
		private EntityManager em;
		  
		    
//		    @Inject
//		    UserTransaction utx;

		    
		   @Test
		   public void shouldBeAbleToInjectMembersIntoTestClass()
				   throws Exception {			   			    			  			   
			   
//			   EntityManager em = PersistenceListener.createEntityManager();
//			   utx.begin();
//		        em.joinTransaction();
			   em.getTransaction().begin();
			   System.out.println("Dumping old records...");
//			   UserTransaction tux = em.getTransaction();
		        em.createQuery("delete from Game").executeUpdate();
		        em.getTransaction().commit();
		        
			   Assert.assertNotNull(cdiTestBean);
			   Assert.assertEquals(cdiTestBean.status, "sampelStatus");
		   }
	}