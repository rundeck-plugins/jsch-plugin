package com.plugin.jschplugin.util;

import com.dtolabs.rundeck.core.common.IFramework;
import com.dtolabs.rundeck.core.common.INodeEntry;
import com.dtolabs.rundeck.core.common.IRundeckProject;
import com.dtolabs.rundeck.core.dispatcher.DataContextUtils;
import com.dtolabs.rundeck.core.execution.ExecutionContext;
import com.dtolabs.rundeck.core.storage.ResourceMeta;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class PropertyResolver {
    private INodeEntry node;
    private IFramework framework;
    private ExecutionContext context;
    private IRundeckProject frameworkProject;

    public PropertyResolver(INodeEntry node, ExecutionContext context) {
        this.node = node;
        this.framework = context.getIFramework();
        this.context = context;
        this.frameworkProject = framework.getFrameworkProjectMgr().getFrameworkProject(
                context.getFrameworkProject());
    }

    public String resolve(final String propName, final String defaultValue) {
        return ResolverUtil.resolveProperty(propName, defaultValue, node, frameworkProject, framework);
    }

    public String resolve(final String propName) {
        return ResolverUtil.resolveProperty(propName, null, node, frameworkProject, framework);
    }

    public Boolean resolveBoolean(final String propName) {
        return ResolverUtil.resolveBooleanProperty(
                propName,
                false,
                node,
                frameworkProject,
                framework
        );
    }

    public InputStream getPrivateKeyStorageData(String property) throws IOException {
        String path = resolve(property);
        //expand properties in path
        if (path != null && path.contains("${")) {
            path = DataContextUtils.replaceDataReferencesInString(path, context.getDataContext());
        }
        if (null == path) {
            return null;
        }
        return context
                .getStorageTree()
                .getResource(path)
                .getContents()
                .getInputStream();

    }

    public String getStoragePath(String property) {
        String path = resolve(property);

        //expand properties in path
        if (path != null && path.contains("${")) {
            path = DataContextUtils.replaceDataReferencesInString(path, context.getDataContext());
        }
        return path;
    }

    public String getPasswordFromPath(String path) throws IOException {
        ResourceMeta contents = context.getStorageTree().getResource(path).getContents();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        contents.writeContent(byteArrayOutputStream);
        String password = new String(byteArrayOutputStream.toByteArray());
        return password;
    }

    public String nonBlank(final String input) {
        if (null == input || "".equals(input.trim())) {
            return null;
        } else {
            return input.trim();
        }
    }

    /**
     * Look for a node/project/framework config property of the given key, and if not found
     * fallback to a framework property, return parsed long or the default
     *
     * @param key           key for node attribute/project/framework property
     * @param frameworkProp fallback framework property
     * @param defval        default value
     * @return parsed value or default
     */
    public Long resolveLongFwk(final String key, final String frameworkProp, final long defval) {
        long timeout = defval;
        String opt = resolve(key);
        if (opt == null && frameworkProp != null && framework.getPropertyLookup().hasProperty(frameworkProp)) {
            opt = framework.getPropertyLookup().getProperty(frameworkProp);
        }
        if (opt != null) {
            try {
                timeout = Long.parseLong(opt);
            } catch (NumberFormatException ignored) {
            }
        }
        return timeout;
    }

    public String evaluateSecureOption(final String optionName, final ExecutionContext context) {
        if (null == optionName) {
            context.getExecutionListener().log(3, "option name was null");
            return null;
        }
        if (null == context.getPrivateDataContext()) {
            context.getExecutionListener().log(3, "private context was null");
            return null;
        }
        final String[] opts = optionName.split("\\.", 2);
        if (null != opts && 2 == opts.length) {
            final Map<String, String> option = context.getPrivateDataContext().get(opts[0]);
            if (null != option) {
                final String value = option.get(opts[1]);
                if (null == value) {
                    context.getExecutionListener().log(3, "private context '" + optionName + "' was null");
                }
                return value;
            } else {
                context.getExecutionListener().log(3, "private context '" + opts[0] + "' was null");
            }
        }
        return null;
    }

    public boolean hasProperty(String property){
        return framework.getPropertyLookup().hasProperty(property);
    }

    public String getProperty(String property){
        return framework.getPropertyLookup().getProperty(property);
    }

}
