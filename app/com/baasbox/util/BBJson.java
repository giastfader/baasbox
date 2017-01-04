/*
 * Copyright (c) 2014.
 *
 * BaasBox - info@baasbox.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.baasbox.util;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.NullNode;

/**
 * Created by Andrea Tortorella on 23/06/14.
 */
public final class BBJson {
    private static final ObjectMapperExt MAPPER = new ObjectMapperExt().registerModule(new MetricsModule(TimeUnit.SECONDS,TimeUnit.SECONDS,false));

    public static class ObjectMapperExt  extends ObjectMapper{

        public JsonNode readTreeOrMissing(String in){
            try {
                return readTree(in);
            } catch (IOException e) {
                return MissingNode.getInstance();
            }
        }

        @Override
        public ObjectMapperExt registerModule(Module module) {
            super.registerModule(module);
            return this;
        }
        
        public String asTextOrNull (JsonNode value){
        	if (isNull(value)) return null;
        	else return value.asText();
        }
        
        public long asLongOrDefault (JsonNode value, long defaultValue){
        	if (isNull(value)) return defaultValue;
        	else return value.asLong(defaultValue);
        }
        
        public int asIntOrDefault (JsonNode value, int defaultValue){
        	if (isNull(value)) return defaultValue;
        	else return value.asInt(defaultValue);
        }
        
        public String[] asArrayOfStringsOrNull (JsonNode value){
        	if (isNull(value) || !value.isArray()) return null;
        	else {
        		String [] toRet = new String[((ArrayNode) value).size()];
        		for (int i=0; i< toRet.length; i++){
        			toRet[i] = asTextOrNull( ((ArrayNode) value).get(i));
        		}
        		return toRet;
        	}
        } //asArrayOfStringsOrNull
        
        public boolean asBooleanOrDefault(JsonNode value, boolean defaultValue){
        	if (isNull(value)) return defaultValue;
        	else return value.asBoolean(defaultValue);
        }
        
        
    } //public static class ObjectMapperExt

    public static String prettyPrinted(JsonNode value){
        try {
            return mapper().writerWithDefaultPrettyPrinter().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return "";
        }
    }
    public static ObjectMapperExt mapper(){
        return MAPPER;
    }
    
    public static boolean isNull (JsonNode value){
    	return
    			value == null
    			|| value instanceof NullNode
    			|| value instanceof MissingNode;
    }
} //BBJson
