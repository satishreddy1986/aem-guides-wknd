package com.adobe.aem.guides.wknd.core.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
       
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.commons.mail.MailTemplate;
//MessageServiceGateway API
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;

import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import org.apache.sling.api.resource.ResourceResolver;
//Sling Imports
import org.apache.sling.api.resource.ResourceResolverFactory ; 
    
 
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
 

@Component(service=WorkflowProcess.class, property = {"process.label=My Email Custom Step"})
public class CustomStep implements WorkflowProcess 
{
           
          
/** Default log. */
protected final Logger log = LoggerFactory.getLogger(this.getClass());
          
@Reference
private ResourceResolverFactory resolverFactory;
  
//Inject a MessageGatewayService 
@Reference
private MessageGatewayService messageGatewayService;
    

private static final String EMAIL_TEMPLATE = "/apps/email-template.txt";

public void execute(WorkItem item, WorkflowSession wfsession,MetaDataMap args) throws WorkflowException {
     
    try
    {
        log.info("Custom mail execute method");    //ensure that the execute method is invoked    
               
        Map<String, Object> param = new HashMap<String, Object>();
        param.put(ResourceResolverFactory.SUBSERVICE, "getResourceResolver");
        ResourceResolver resourceResolver = resolverFactory.getServiceResourceResolver(param);
        log.info("resourceResolver " + resourceResolver);
        Node templateNode = resourceResolver.getResource(EMAIL_TEMPLATE).adaptTo(Node.class);
        log.info("templateNode " + templateNode);
		final Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("title", "Demo Email");
		parameters.put("name", "Sathish");
		parameters.put("id", "0001");
		parameters.put("host.prefix", "http://localhost");
		parameters.put("faqpath", "/content/AEM63App/faq.html");
		final MailTemplate mailTemplate = MailTemplate.create(EMAIL_TEMPLATE, templateNode.getSession());
		log.info("mailTemplate " + mailTemplate);
		HtmlEmail email = mailTemplate.getEmail(StrLookup.mapLookup(parameters), HtmlEmail.class);
		log.info("email " + email);
		email.setSubject("AEM - Demo Email for Templated email");
		email.addTo("satishreddy.ravula@gmail.com");
		MessageGateway<HtmlEmail> messageGateway = messageGatewayService.getGateway(HtmlEmail.class);
		messageGateway.send(email);
    }
       
        catch (Exception e)
        {
        log.error("Exception in Custom Step " + e);
        }
     }
      
    }