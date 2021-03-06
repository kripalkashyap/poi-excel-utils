/*
 * Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hellojavaer.poi.excel.utils.write;

import java.util.HashMap;

import org.hellojavaer.poi.excel.utils.read.ExcelReadCellValueMapping;

/**
 * Config the mapping between source value(which must be convertd to type of
 * String) to target value.<br>
 * If you have used this configration, it also requries that very source value
 * must have a target value. If not, {@code #ExcelWriteException} will been
 * thrown. To process this case, you can use 'setDefaultValue' method or
 * 'setDefaultProcessor' to set default value.
 * 
 * @see ExcelReadCellValueMapping
 * @author <a href="mailto:hellojavaer@gmail.com">zoukaiming</a>
 */
public class ExcelWriteCellValueMapping extends HashMap<String, Object> {

    private static final long       serialVersionUID   = 1L;

    private static final Object     DEFAULT_INPUT      = new Object();
    private boolean                 settedDefaultValue = false;
    private Object                  defaultValue       = null;
    @SuppressWarnings("rawtypes")
    private ExcelWriteCellProcessor defaultProcessor;

    public Object getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(Object val) {
        this.defaultValue = val;
        this.settedDefaultValue = true;
    }

    public void setDefaultValueWithDefaultInput() {
        this.defaultValue = DEFAULT_INPUT;
        this.settedDefaultValue = true;
    }

    public void resetDefaultValue() {
        this.defaultValue = null;
        this.settedDefaultValue = false;
    }

    public boolean isSettedDefaultValue() {
        return settedDefaultValue;
    }

    public boolean isSettedDefaultValueWithDefaultInput() {
        return settedDefaultValue && (defaultValue == DEFAULT_INPUT);
    }

    @SuppressWarnings("rawtypes")
    public ExcelWriteCellProcessor getDefaultProcessor() {
        return defaultProcessor;
    }

    public void setDefaultProcessor(@SuppressWarnings("rawtypes")
    ExcelWriteCellProcessor defaultProcessor) {
        this.defaultProcessor = defaultProcessor;
    }
}
