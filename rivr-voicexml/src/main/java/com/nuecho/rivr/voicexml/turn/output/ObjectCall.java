/*
 * Copyright (c) 2013 Nu Echo Inc. All rights reserved.
 */

package com.nuecho.rivr.voicexml.turn.output;

import static com.nuecho.rivr.core.util.Assert.*;
import static com.nuecho.rivr.voicexml.rendering.voicexml.VoiceXmlDomUtil.*;
import static java.util.Collections.*;

import java.util.*;

import javax.json.*;

import org.w3c.dom.*;

import com.nuecho.rivr.core.util.*;
import com.nuecho.rivr.voicexml.dialogue.*;
import com.nuecho.rivr.voicexml.rendering.voicexml.*;
import com.nuecho.rivr.voicexml.turn.output.fetch.*;
import com.nuecho.rivr.voicexml.util.json.*;

/**
 * An {@link ObjectCall} is a {@link VoiceXmlOutputTurn} used to exploit
 * platform-specific functionality.
 * 
 * @author Nu Echo Inc.
 * @see <a
 *      href="https://www.w3.org/TR/voicexml20/#dml2.3.5">https://www.w3.org/TR/voicexml20/#dml2.3.5</a>
 */
public class ObjectCall extends VoiceXmlOutputTurn {
    public static final String OBJECT_RESULT_VARIABLE_NAME = "object";

    private static final String OBJECT_TURN_TYPE = "object";

    private static final String ARCHIVES_PROPERTY = "archives";
    private static final String PARAMETERS_PROPERTY = "parameters";
    private static final String POST_OBJECT_SCRIPT_PROPERTY = "postObjectScript";
    private static final String TYPE_PROPERTY = "type";
    private static final String DATA_PROPERTY = "data";
    private static final String CODE_TYPE_PROPERTY = "codeType";
    private static final String CODE_BASE_PROPERTY = "codeBase";
    private static final String CLASS_ID_PROPERTY = "classId";
    private static final String FETCH_CONFIGURATION_PROPERTY = "fetchConfiguration";

    private String mClassId;
    private String mCodeBase;
    private String mCodeType;
    private String mData;
    private String mType;
    private List<String> mArchives;
    private FetchConfiguration mFetchConfiguration;
    private List<Parameter> mParameters = Collections.emptyList();
    private String mPostObjectScript;

    /**
     * @param name The name of this turn. Not empty.
     */
    public ObjectCall(String name) {
        super(name);
    }

    /**
     * @param classId The URI specifying the location of the object's
     *            implementation.
     */
    public final void setClassId(String classId) {
        mClassId = classId;
    }

    /**
     * @param codeBase The base path used to resolve relative URIs specified by
     *            classid, data, and archive.
     */
    public final void setCodeBase(String codeBase) {
        mCodeBase = codeBase;
    }

    /**
     * @param codeType The content type of data expected when downloading the
     *            object specified by classid.
     */
    public final void setCodeType(String codeType) {
        mCodeType = codeType;
    }

    /**
     * @param data The URI specifying the location of the object's data.
     */
    public final void setData(String data) {
        mData = data;
    }

    /**
     * @param type The content type of the data specified by the data attribute
     */
    public final void setType(String type) {
        mType = type;
    }

    /**
     * @param archives A list of URIs for archives containing resources relevant
     *            to the object.
     */
    public final void setArchives(List<String> archives) {
        mArchives = archives;
    }

    /**
     * @param fetchConfiguration The object {@link FetchConfiguration}.
     */
    public final void setFetchConfiguration(FetchConfiguration fetchConfiguration) {
        mFetchConfiguration = fetchConfiguration;
    }

    /**
     * @param parameters A list of parameters passed when invoking object. Not
     *            null.
     */
    public final void setParameters(List<Parameter> parameters) {
        Assert.noNullValues(parameters, "parameters");
        mParameters = new ArrayList<Parameter>(parameters);
    }

    /**
     * @param parameters A list of parameters passed when invoking object. Not
     *            null.
     */
    public final void setParameters(Parameter... parameters) {
        setParameters(asListChecked(parameters));
    }

    /**
     * @param postObjectScript The ECMAScript script to execute after object
     *            invocation.
     */
    public final void setPostObjectScript(String postObjectScript) {
        mPostObjectScript = postObjectScript;
    }

    public final String getClassId() {
        return mClassId;
    }

    public final String getCodeBase() {
        return mCodeBase;
    }

    public final String getCodeType() {
        return mCodeType;
    }

    public final String getData() {
        return mData;
    }

    public final String getType() {
        return mType;
    }

    public final List<String> getArchives() {
        return mArchives;
    }

    public final FetchConfiguration getFetchConfiguration() {
        return mFetchConfiguration;
    }

    public final List<Parameter> getParameters() {
        return unmodifiableList(mParameters);
    }

    public final String getPostObjectScript() {
        return mPostObjectScript;
    }

    @Override
    protected final String getOuputTurnType() {
        return OBJECT_TURN_TYPE;
    }

    @Override
    protected void addTurnProperties(JsonObjectBuilder builder) {
        JsonUtils.add(builder, CLASS_ID_PROPERTY, mClassId);
        JsonUtils.add(builder, CODE_BASE_PROPERTY, mCodeBase);
        JsonUtils.add(builder, CODE_TYPE_PROPERTY, mCodeType);
        JsonUtils.add(builder, DATA_PROPERTY, mData);
        JsonUtils.add(builder, TYPE_PROPERTY, mType);
        JsonUtils.add(builder, POST_OBJECT_SCRIPT_PROPERTY, mPostObjectScript);

        JsonUtils.add(builder, PARAMETERS_PROPERTY, JsonUtils.toJson(mParameters));
        JsonUtils.add(builder, FETCH_CONFIGURATION_PROPERTY, mFetchConfiguration);

        if (mArchives != null) {
            JsonArrayBuilder archiveBuilder = JsonUtils.createArrayBuilder();
            for (String archive : mArchives) {
                archiveBuilder.add(archive);
            }
            JsonUtils.add(builder, ARCHIVES_PROPERTY, archiveBuilder);
        }
    }

    @Override
    protected void fillVoiceXmlDocument(Document document, Element formElement, VoiceXmlDialogueContext dialogueContext)
            throws VoiceXmlDocumentRenderingException {

        Element objectElement = DomUtils.appendNewElement(formElement, OBJECT_ELEMENT);
        objectElement.setAttribute(NAME_ATTRIBUTE, OBJECT_FORM_ITEM_NAME);

        List<String> archives = getArchives();
        if (archives != null && !archives.isEmpty()) {
            objectElement.setAttribute(ARCHIVE_ATTRIBUTE, StringUtils.join(archives, " "));
        }

        setAttribute(objectElement, CLASS_ID_ATTRIBUTE, mClassId);
        setAttribute(objectElement, CODE_BASE_ATTRIBUTE, mCodeBase);
        setAttribute(objectElement, CODE_TYPE_ATTRIBUTE, mCodeType);
        setAttribute(objectElement, DATA_ATTRIBUTE, mData);
        setAttribute(objectElement, TYPE_ATTRIBUTE, mType);

        for (Parameter parameter : mParameters) {
            Element paramElement = DomUtils.appendNewElement(objectElement, PARAM_ELEMENT);
            paramElement.setAttribute(NAME_ATTRIBUTE, parameter.getName());

            setAttribute(paramElement, VALUE_ATTRIBUTE, parameter.getValue());
            setAttribute(paramElement, EXPR_ATTRIBUTE, parameter.getExpression());
            setAttribute(paramElement, TYPE_ATTRIBUTE, parameter.getType());
            ParameterValueType valueType = parameter.getValueType();
            if (valueType != null) {
                paramElement.setAttribute(VALUE_TYPE_ATTRIBUTE, valueType.name());
            }
        }

        FetchConfiguration fetchConfiguration = mFetchConfiguration;
        if (fetchConfiguration != null) {
            applyRessourceFetchConfiguration(objectElement, fetchConfiguration);
        }

        Element filledElement = DomUtils.appendNewElement(objectElement, FILLED_ELEMENT);

        createVarElement(filledElement, OBJECT_RESULT_VARIABLE_NAME, "dialog." + OBJECT_FORM_ITEM_NAME);

        if (getPostObjectScript() != null) {
            createScript(filledElement, mPostObjectScript);
        }

        createScript(filledElement, RIVR_SCOPE_OBJECT + ".addValueResult(" + OBJECT_RESULT_VARIABLE_NAME + ");");
        createGotoSubmit(filledElement);
    }

    /**
     * {@link ObjectCall} parameter, can be created with a string value, a
     * {@link JsonValue} or an expression.
     */
    public static final class Parameter implements JsonSerializable {
        private static final String NAME_PROPERTY = "name";
        private static final String VALUE_PROPERTY = "value";
        @SuppressWarnings("hiding")
        private static final String TYPE_PROPERTY = "type";
        private static final String VALUE_TYPE_PROPERTY = "valueType";
        private static final String EXPRESSION_PROPERTY = "expression";

        private final String mName;
        private String mExpression;
        private String mValue;
        private ParameterValueType mValueType;
        private String mType;

        /**
         * @param name The name of the parameter. Not empty.
         * @param value The string value of the parameter. Not null.
         * @return The newly created object parameter
         */
        public static Parameter createWithValue(String name, String value) {
            Assert.notEmpty(name, "name");
            Assert.notNull(value, "value");

            Parameter parameter = new Parameter(name);
            parameter.mValue = value;
            return parameter;
        }

        /**
         * @param name The name of the parameter. Not empty.
         * @param expression The ECMAScript expression of the parameter. Not
         *            null.
         * @return The newly created object parameter
         */
        public static Parameter createWithExpression(String name, String expression) {
            Assert.notEmpty(name, "name");
            Assert.notNull(expression, "expression");

            Parameter parameter = new Parameter(name);
            parameter.mExpression = expression;
            return parameter;
        }

        /**
         * @param name The name of the parameter. Not empty.
         * @param json The JSON value of the parameter. Not null.
         * @return The newly created object parameter
         */
        public static Parameter createWithJson(String name, JsonValue json) {
            Assert.notEmpty(name, "name");
            Assert.notNull(json, "json");

            return createWithExpression(name, json.toString());
        }

        private Parameter(String name) {
            mName = name;
        }

        /**
         * @param valueType One of {@link ParameterValueType#data} or
         *            {@link ParameterValueType#ref}. Indicates to an object if
         *            the value associated with name is data or a URI.
         *            <code>null</code> to use the VoiceXML platform default.
         */
        public void setValueType(ParameterValueType valueType) {
            mValueType = valueType;
        }

        /**
         * @param type The media type of the result provided by a URI if the
         *            value type is {@link ParameterValueType#ref}.
         *            <code>null</code> to use the VoiceXML platform default
         */
        public void setType(String type) {
            mType = type;
        }

        public String getName() {
            return mName;
        }

        public String getExpression() {
            return mExpression;
        }

        public String getValue() {
            return mValue;
        }

        public ParameterValueType getValueType() {
            return mValueType;
        }

        public String getType() {
            return mType;
        }

        @Override
        public JsonValue asJson() {
            JsonObjectBuilder builder = JsonUtils.createObjectBuilder();
            JsonUtils.add(builder, NAME_PROPERTY, mName);
            JsonUtils.add(builder, EXPRESSION_PROPERTY, mExpression);
            JsonUtils.add(builder, VALUE_PROPERTY, mValue);
            JsonUtils.add(builder, TYPE_PROPERTY, mType);
            JsonUtils.add(builder, VALUE_TYPE_PROPERTY, mValueType);
            return builder.build();
        }
    }

    /**
     * Types of parameter for &lt;object&gt;.
     * 
     * @author Nu Echo Inc.
     */
    public enum ParameterValueType implements JsonSerializable {
        data, ref;

        @Override
        public JsonValue asJson() {
            return JsonUtils.wrap(name());
        }
    }

    /**
     * Builder used to ease the creation of instances of {@link ObjectCall}.
     */
    public static class Builder {

        private final String mName;
        private String mClassId;
        private String mCodeBase;
        private String mCodeType;
        private String mData;
        private String mType;
        private final List<String> mArchives = new ArrayList<String>();
        private final List<Parameter> mParameters = new ArrayList<Parameter>();
        private FetchConfiguration mFetchConfiguration;
        private String mPostObjectScript;

        public Builder(String name) {
            mName = name;
        }

        public Builder setClassId(String classId) {
            Assert.notNull(classId, "classId");
            mClassId = classId;
            return this;
        }

        public Builder setCodeBase(String codeBase) {
            mCodeBase = codeBase;
            return this;
        }

        public Builder setData(String data) {
            mData = data;
            return this;
        }

        public Builder setType(String type) {
            mType = type;
            return this;
        }

        public Builder setCodeType(String codeType) {
            mCodeType = codeType;
            return this;
        }

        public Builder setFetchConfiguration(FetchConfiguration fetchConfiguration) {
            mFetchConfiguration = fetchConfiguration;
            return this;
        }

        public Builder addArchive(String archive) {
            Assert.notNull(archive, "archive");
            mArchives.add(archive);
            return this;
        }

        public Builder addParameter(Parameter parameter) {
            Assert.notNull(parameter, "parameter");
            mParameters.add(parameter);
            return this;
        }

        public Builder setPostObjectScript(String postObjectScript) {
            mPostObjectScript = postObjectScript;
            return this;
        }

        public ObjectCall build() {
            ObjectCall objectCall = new ObjectCall(mName);
            objectCall.setArchives(mArchives);
            objectCall.setClassId(mClassId);
            objectCall.setCodeBase(mCodeBase);
            objectCall.setCodeType(mCodeType);
            objectCall.setData(mData);
            objectCall.setFetchConfiguration(mFetchConfiguration);
            objectCall.setParameters(mParameters);
            objectCall.setPostObjectScript(mPostObjectScript);
            objectCall.setType(mType);
            return objectCall;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((mArchives == null) ? 0 : mArchives.hashCode());
        result = prime * result + ((mClassId == null) ? 0 : mClassId.hashCode());
        result = prime * result + ((mCodeBase == null) ? 0 : mCodeBase.hashCode());
        result = prime * result + ((mCodeType == null) ? 0 : mCodeType.hashCode());
        result = prime * result + ((mData == null) ? 0 : mData.hashCode());
        result = prime * result + ((mFetchConfiguration == null) ? 0 : mFetchConfiguration.hashCode());
        result = prime * result + ((mParameters == null) ? 0 : mParameters.hashCode());
        result = prime * result + ((mPostObjectScript == null) ? 0 : mPostObjectScript.hashCode());
        result = prime * result + ((mType == null) ? 0 : mType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        ObjectCall other = (ObjectCall) obj;
        if (mArchives == null) {
            if (other.mArchives != null) return false;
        } else if (!mArchives.equals(other.mArchives)) return false;
        if (mClassId == null) {
            if (other.mClassId != null) return false;
        } else if (!mClassId.equals(other.mClassId)) return false;
        if (mCodeBase == null) {
            if (other.mCodeBase != null) return false;
        } else if (!mCodeBase.equals(other.mCodeBase)) return false;
        if (mCodeType == null) {
            if (other.mCodeType != null) return false;
        } else if (!mCodeType.equals(other.mCodeType)) return false;
        if (mData == null) {
            if (other.mData != null) return false;
        } else if (!mData.equals(other.mData)) return false;
        if (mFetchConfiguration == null) {
            if (other.mFetchConfiguration != null) return false;
        } else if (!mFetchConfiguration.equals(other.mFetchConfiguration)) return false;
        if (mParameters == null) {
            if (other.mParameters != null) return false;
        } else if (!mParameters.equals(other.mParameters)) return false;
        if (mPostObjectScript == null) {
            if (other.mPostObjectScript != null) return false;
        } else if (!mPostObjectScript.equals(other.mPostObjectScript)) return false;
        if (mType == null) {
            if (other.mType != null) return false;
        } else if (!mType.equals(other.mType)) return false;
        return true;
    }

}
