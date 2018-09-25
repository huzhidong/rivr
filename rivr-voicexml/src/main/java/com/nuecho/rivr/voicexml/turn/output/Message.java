/*
 * Copyright (c) 2013 Nu Echo Inc. All rights reserved.
 */

package com.nuecho.rivr.voicexml.turn.output;

import static com.nuecho.rivr.core.util.Assert.*;
import static com.nuecho.rivr.voicexml.rendering.voicexml.VoiceXmlDomUtil.*;

import java.util.*;

import javax.json.*;

import org.w3c.dom.*;

import com.nuecho.rivr.core.util.*;
import com.nuecho.rivr.voicexml.dialogue.*;
import com.nuecho.rivr.voicexml.rendering.voicexml.*;
import com.nuecho.rivr.voicexml.turn.output.audio.*;
import com.nuecho.rivr.voicexml.util.json.*;

/**
 * A {@link Message} is a {@link VoiceXmlOutputTurn} that plays a sequence of
 * {@link AudioItem}.
 *
 * @author Nu Echo Inc.
 * @see AudioItem
 * @see <a href=
 *      "https://www.w3.org/TR/voicexml20/#dml4.1.8">https://www.w3.org/TR/voicexml20/#dml4.1.8</a>
 */
public class Message extends VoiceXmlOutputTurn {
    private static final String MESSAGE_TURN_TYPE = "message";

    private static final String BARGE_IN_PROPERTY = "bargeIn";
    private static final String LANGUAGE_PROPERTY = "language";
    private static final String AUDIO_ITEMS_PROPERTY = "audioItems";

    private final List<AudioItem> mAudioItems;
    private String mLanguage;
    private Boolean mBargeIn;

    /**
     * @param name The name of this turn. Not empty.
     * @param audioItems The sequence of {@link AudioItem} to play. Not empty.
     */
    public Message(String name, List<AudioItem> audioItems) {
        super(name);
        Assert.notEmpty(audioItems, "audioItems");
        mAudioItems = new ArrayList<AudioItem>(audioItems);
    }

    /**
     * @param name The name of this turn. Not empty.
     * @param audioItems The sequence of {@link AudioItem} to play. Not empty.
     */
    public Message(String name, AudioItem... audioItems) {
        this(name, asListChecked(audioItems));
    }

    /**
     * @param language The language identifier (e.g. "en-US") for the message.
     *            <code>null</code> to use the VoiceXML platform default
     */
    public final void setLanguage(String language) {
        mLanguage = language;
    }

    /**
     * @param bargeIn <ul>
     *            <li>{@link Boolean#TRUE} to enable barge-in</li>
     *            <li>
     *            {@link Boolean#FALSE} to disable barge-in</li>
     *            <li><code>null</code> to use the VoiceXML platform default</li>
     *            </ul>
     */
    public final void setBargeIn(Boolean bargeIn) {
        mBargeIn = bargeIn;
    }

    public final List<AudioItem> getAudioItems() {
        return Collections.unmodifiableList(mAudioItems);
    }

    public final String getLanguage() {
        return mLanguage;
    }

    public final Boolean getBargeIn() {
        return mBargeIn;
    }

    @Override
    protected final String getOuputTurnType() {
        return MESSAGE_TURN_TYPE;
    }

    @Override
    protected void addTurnProperties(JsonObjectBuilder builder) {
        JsonUtils.add(builder, AUDIO_ITEMS_PROPERTY, JsonUtils.toJson(mAudioItems));
        JsonUtils.add(builder, LANGUAGE_PROPERTY, mLanguage);
        if (mBargeIn == null) {
            builder.addNull(BARGE_IN_PROPERTY);
        } else {
            builder.add(BARGE_IN_PROPERTY, mBargeIn.booleanValue());
        }
    }

    @Override
    protected void fillVoiceXmlDocument(Document document, Element formElement, VoiceXmlDialogueContext dialogueContext)
            throws VoiceXmlDocumentRenderingException {
        Element blockElement = addBlockElement(formElement);
        createPrompt(mLanguage, blockElement, dialogueContext, mBargeIn, mAudioItems);
        createGotoSubmit(blockElement);
    }

    /**
     * Builder used to ease the creation of instances of {@link Message}.
     */
    public static class Builder {

        private final String mName;
        private final List<AudioItem> mAudioItems = new ArrayList<AudioItem>();
        private String mLanguage;
        private Boolean mBargeIn;

        public Builder(String name) {
            mName = name;
        }

        /**
         * @deprecated Use {@link #addAudioItem(AudioItem)} instead
         */
        @Deprecated
        public Builder addAudio(AudioItem audioItem) {
            return addAudioItem(audioItem);
        }

        /**
         * @since 1.0.1
         */
        public Builder addAudioItem(AudioItem audioItem) {
            Assert.notNull(audioItem, "audioItem");
            mAudioItems.add(audioItem);
            return this;
        }

        /**
         * @since 1.0.1
         */
        public Builder addAudioItems(AudioItem... audioItems) {
            return addAudioItems(asListChecked(audioItems));
        }

        public Builder addAudioItems(List<AudioItem> audioItems) {
            Assert.noNullValues(audioItems, mLanguage);
            mAudioItems.addAll(audioItems);
            return this;
        }

        public Builder setLanguage(String language) {
            mLanguage = language;
            return this;
        }

        public Builder setBargein(Boolean bargeIn) {
            mBargeIn = bargeIn;
            return this;
        }

        public Message build() {
            Message message = new Message(mName, mAudioItems);
            message.setBargeIn(mBargeIn);
            message.setLanguage(mLanguage);
            return message;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((mAudioItems == null) ? 0 : mAudioItems.hashCode());
        result = prime * result + ((mBargeIn == null) ? 0 : mBargeIn.hashCode());
        result = prime * result + ((mLanguage == null) ? 0 : mLanguage.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        Message other = (Message) obj;
        if (mAudioItems == null) {
            if (other.mAudioItems != null) return false;
        } else if (!mAudioItems.equals(other.mAudioItems)) return false;
        if (mBargeIn == null) {
            if (other.mBargeIn != null) return false;
        } else if (!mBargeIn.equals(other.mBargeIn)) return false;
        if (mLanguage == null) {
            if (other.mLanguage != null) return false;
        } else if (!mLanguage.equals(other.mLanguage)) return false;
        return true;
    }

}
