package arquillilan.tomcat.test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;

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

import javax.naming.*;


	@RunWith(Arquillian.class)
	public class HibernateTestSapmle2 {
		   
		   @Deployment
		   public static WebArchive createTestArchive()
		   {
			   MavenDependencyResolver resolver = DependencyResolvers.use(
					    MavenDependencyResolver.class).loadMetadataFromPom("pom.xml");
			   
			   WebArchive webArchive=  ShrinkWrap
		            .create(WebArchive.class, "ROOT.war")
		            .addClasses(Game.class,PersistenceListener.class)
		            .addAsLibraries(
		                   		resolver.artifact("org.jboss.weld.servlet:weld-servlet")
		                   		.artifact("org.apache.tomcat:tomcat-dbcp")
		                   		.artifact("org.hibernate:hibernate-entitymanager")
		                   		.artifact("org.hibernate:hibernate-validator")
		                   		.artifact("org.hibernate:hibernate-core")	
		                   		.artifact("com.h2database:h2")
		                   		.resolveAs(GenericArchive.class))
		                    
		            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
		            .addAsWebInfResource("test-persistence.xml", "classes/META-INF/persistence.xml")
		            .addAsManifestResource("context.xml", "context.xml")
		            .setWebXML("hibernate-web.xml");
		      System.out.println(webArchive.toString(true));
		      
		      return webArchive;
		   }

		   
		    private static final String[] GAME_TITLES = {
		        "Super Mario Brothers",
		        "Mario Kart",
		        "F-Zero"
		    };		    		    				 
			    
		    @Inject
			private EntityManager em;
			    
			   @Test
			   public void testingWithJPQL()
					   throws Exception {			   			    			  			   
				   
				    em.getTransaction().begin();
				    System.out.println("Dumping old records...");
			        em.createQuery("delete from Game").executeUpdate();
			        em.getTransaction().commit();
			        
			        /**        ''''''''''''''        **/
			        
			        em.getTransaction().begin();
			        System.out.println("Inserting records...");
			        for (String title : GAME_TITLES) {
			            Game game = new Game(title);
			            em.persist(game);
			        }
			        em.getTransaction().commit();


			        /**        ''''''''''''''        **/
			        
			        em.getTransaction().begin();
			        String fetchingAllGamesInJpql = "select g from Game g order by g.id";
			     
			        System.out.println("Selecting (using JPQL)...");
			        
			        List<Game> games = em.createQuery(fetchingAllGamesInJpql, Game.class).getResultList();
			        
			        System.out.println("Found " + games.size() + " games (using JPQL):");
			        
			        assertContainsAllGames(games);	
			        em.getTransaction().commit();
			   }
			   
			   
			   private static void assertContainsAllGames(Collection<Game> retrievedGames) {
			        Assert.assertEquals(GAME_TITLES.length, retrievedGames.size());
			        final Set<String> retrievedGameTitles = new HashSet<String>();
			        for (Game game : retrievedGames) {
			            System.out.println("* " + game);
			            retrievedGameTitles.add(game.getTitle());
			        }
			        Assert.assertTrue(retrievedGameTitles.containsAll(Arrays.asList(GAME_TITLES)));
			    }			   
	}