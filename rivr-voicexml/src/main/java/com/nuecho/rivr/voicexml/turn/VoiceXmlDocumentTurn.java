/*
 * Copyright (c) 2013 Nu Echo Inc. All rights reserved.
 */

package com.nuecho.rivr.voicexml.turn;

import java.util.*;

import javax.json.*;

import org.w3c.dom.*;

import com.nuecho.rivr.core.util.*;
import com.nuecho.rivr.voicexml.dialogue.*;
import com.nuecho.rivr.voicexml.rendering.voicexml.*;
import com.nuecho.rivr.voicexml.turn.last.*;
import com.nuecho.rivr.voicexml.turn.output.*;
import com.nuecho.rivr.voicexml.util.json.*;

/**
 * Base class for {@link VoiceXmlOutputTurn} and {@link VoiceXmlLastTurn}. A
 * {@link VoiceXmlDocumentTurn} has a <code>name</code> and a list (possibly
 * empty) of {@link VoiceXmlDocumentAdapter}.
 *
 * @author Nu Echo Inc.
 */
public abstract class VoiceXmlDocumentTurn implements JsonSerializable {

    private static final String DATA_PROPERTY = "data";
    private static final String NAME_PROPERTY = "name";

    private final String mName;

    private final List<VoiceXmlDocumentAdapter> mAdapters = new ArrayList<VoiceXmlDocumentAdapter>();

    public VoiceXmlDocumentTurn(String name) {
        Assert.notEmpty(name, "name");
        mName = name;
    }

    protected abstract Document createVoiceXmlDocument(VoiceXmlDialogueContext dialogueContext)
            throws VoiceXmlDocumentRenderingException;

    /**
     * Adds top level properties to the JSON representation of this turn
     *
     * @param builder A {@link JsonObjectBuilder} that can be used to create the
     *            top level JSON properties
     */
    protected void addTopLevelProperties(JsonObjectBuilder builder) {}

    protected abstract void addTurnProperties(JsonObjectBuilder builder);

    public final void addAdapter(VoiceXmlDocumentAdapter adapter) {
        mAdapters.add(adapter);
    }

    public final String getName() {
        return mName;
    }

    public final Document getVoiceXmlDocument(VoiceXmlDialogueContext dialogueContext)
            throws VoiceXmlDocumentRenderingException {
        Document document = createVoiceXmlDocument(dialogueContext);
        for (VoiceXmlDocumentAdapter adapter : mAdapters) {
            adapter.adaptVoiceXmlDocument(document);
        }
        return document;
    }

    @Override
    public final String toString() {
        return asJson().toString();
    }

    @Override
    public final JsonValue asJson() {
        JsonObjectBuilder builder = JsonUtils.createObjectBuilder();
        JsonObjectBuilder dataBuilder = JsonUtils.createObjectBuilder();
        addTurnProperties(dataBuilder);

        JsonUtils.add(builder, NAME_PROPERTY, getName());
        JsonUtils.add(builder, DATA_PROPERTY, dataBuilder.build());
        addTopLevelProperties(builder);
        return builder.build();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mAdapters == null) ? 0 : mAdapters.hashCode());
        result = prime * result + ((mName == null) ? 0 : mName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        VoiceXmlDocumentTurn other = (VoiceXmlDocumentTurn) obj;
        if (mAdapters == null) {
            if (other.mAdapters != null) return false;
        } else if (!mAdapters.equals(other.mAdapters)) return false;
        if (mName == null) {
            if (other.mName != null) return false;
        } else if (!mName.equals(other.mName)) return false;
        return true;
    }

}