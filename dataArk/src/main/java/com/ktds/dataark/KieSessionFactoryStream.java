package com.ktds.dataark;

import java.io.IOException;
import java.io.Serializable;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.KieScanner;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.io.KieResources;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import org.drools.core.io.impl.UrlResource;
import org.drools.compiler.kproject.ReleaseIdImpl;
import java.io.InputStream;

public class KieSessionFactoryStream implements Serializable {
    KieSession ksession;
    private static KieContainer kContainer = null;
    private static KieScanner kScanner = null;

    public static KieSession getKieSession() {
	if (ksession == null)
	    ksession = getNewKieSession();
	return ksession;
    }

    public static ksession getNewKieSession() {
	//String url = "http://test06.bigfence.com:8580/kie-wb/maven2/com/cloudera/sprue/1.0/sprue-1.0.jar";
	KieServices kieServices = KieServices.Factory.get();
	KieRepository kieRepository = kieServices.getRepository();

	//UrlResource urlResource = (UrlResource)kieServices.getResources().newUrlResource(url);
	//urlResource.setUsername("workbench");
	//urlResource.setPassword("workbench1!");
	//urlResource.setBasicAuthentication("enabled");

	//InputStream is = null;
	KieModule kieModule = null;
	try {
		//is = urlResource.getInputStream();
		//kieModule = kieRepository.addKieModule(kieServices.getResources().newInputStreamResource(is));
		kContainer = kieServices.newKieContainer(kieServices.newReleaseId("com.cloudera", "sprue", "LATEST"));
		kScanner = kieServices.newKieScanner(kContainer);
		kScanner.start(10000L);
	} catch ( Exception e ) {
		System.out.println("Error - " + e.getMessage());
	}

	KieBaseConfiguration config = kieServices.newKieBaseConfiguration();
	config.setOption(EventProcessingOption.STREAM);

	if ( kContainer == null ) return null;
	KieBase kbase = kContainer.newKieBase(config);
	return kbase.newKieSession();
	
    }
}
