package com.raffle.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Basic Controller which is called for unhandled errors
 */
@Controller
public class AppErrorController implements ErrorController{

    /**
     * Error Attributes in the Application
     */
    private ErrorAttributes errorAttributes;
    
	// inject via application.properties
	@Value("${applicationName}")
	private String applicationName = "";

    private final static String ERROR_PATH = "/error";

    /**
     * Controller for the Error Controller
     * @param errorAttributes
     */
    public AppErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    /**
     * Supports the HTML Error View
     * @param request
     * @return
     */
    @RequestMapping(value = ERROR_PATH, produces = "text/html")
    public String errorHtml(Map<String, Object> model, HttpServletRequest request) {
    	ArrayList<String> errorAttributes = new ArrayList<String>();
    	Map attributes = getErrorAttributes(request, false);
    	
    	Set keySet = attributes.keySet();
    	
    	Iterator keySetIterator = keySet.iterator();
    	
    	while (keySetIterator.hasNext()) {
    		Object key = keySetIterator.next();
    		errorAttributes.add(key.toString() + " = " + attributes.get(key).toString());
    	}
    	
    	model.put("errorAttributes", errorAttributes.toArray(new String[0]));
    	
    	RaffleApplication.addPageTitles(model, applicationName, "Error");
    	
    	return "error";
    }

    /**
     * Supports other formats like JSON, XML
     * @param request
     * @return
     */
    @RequestMapping(value = ERROR_PATH)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        Map<String, Object> body = getErrorAttributes(request, getTraceParameter(request));
        HttpStatus status = getStatus(request);
        return new ResponseEntity<Map<String, Object>>(body, status);
    }

    /**
     * Returns the path of the error page.
     *
     * @return the error path
     */
    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }


    private boolean getTraceParameter(HttpServletRequest request) {
        String parameter = request.getParameter("trace");
        if (parameter == null) {
            return false;
        }
        return !"false".equals(parameter.toLowerCase());
    }

    private Map<String, Object> getErrorAttributes(HttpServletRequest request,
                                                   boolean includeStackTrace) {
        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
        return this.errorAttributes.getErrorAttributes(requestAttributes,
                includeStackTrace);
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request
                .getAttribute("javax.servlet.error.status_code");
        if (statusCode != null) {
            try {
                return HttpStatus.valueOf(statusCode);
            }
            catch (Exception ex) {
            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}