package com.lumi.bigdata.web.utils.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ServletContextAware;

import com.lumi.largedata.utils.JsonUtil;

public class LanguageResourceInitializer implements ServletContextAware {

	protected static final Logger LOG = LoggerFactory.getLogger(LanguageResourceInitializer.class);

	private List<String> resourceLocations;

	public List<String> getResourceLocations() {
		return resourceLocations;
	}

	public void setResourceLocations(List<String> resourceLocations) {
		this.resourceLocations = resourceLocations;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		writeProperties2JsFile(Constants.LANGUAGE_ZH, servletContext);
		writeProperties2JsFile(Constants.LANGUAGE_EN, servletContext);
	}
	
	/**
	 * 读取properties文件内容
	 */
	private Map<String, String> loadProperties(String language) {
		Locale locale = Constants.LANGUAGE_EN.equalsIgnoreCase(language) ? Locale.ENGLISH : Locale.CHINESE;
		Map<String, String> map = new HashMap<String, String>();
		ResourceBundle bundle = null;
		Set<String> keys = null;
		for (String baseName : resourceLocations) {
			bundle = ResourceBundle.getBundle(baseName, locale);
			keys = bundle.keySet();
			for (String key : keys) {
				map.put(key, bundle.getString(key));
			}
		}
		return map;
	}

	/**
	 * 将中英文内容写入js文件
	 */
	private void writeProperties2JsFile(String language, ServletContext servletContext) {
		Map<String, String> properties = loadProperties(language);
		MessageCache.put(language, properties); // 放入缓存
		BufferedWriter out = null;
		try {
			String path = servletContext.getRealPath("/");
			File file = new File(path + File.separator + "message_" + language + ".js");
			if (!file.exists()) {
				file.createNewFile();
			}
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
			out.write("var messageResource = " + JsonUtil.getJsonFromObj(properties));
			out.flush();
		} catch (IOException e) {
			LOG.error("writeProperties2JsFile", e);
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				LOG.error("writeProperties2JsFile", e);
			}
		}
	}

}
