/*
 * blitzspot
 * Copyright (C) 2018-2019 Peter Hanula
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.deletethis.blitzspot.app.fragments.info;

import org.deletethis.blitzspot.app.R;
import org.deletethis.search.parser.ErrorCode;
import org.deletethis.search.parser.PropertyName;
import org.deletethis.search.parser.PropertyValue;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.StringRes;

class ParserTranslations {
    private static final Map<ErrorCode, Integer> e;
    private static final Map<PropertyName, Integer> p;
    private static final Map<PropertyValue.Predefined, Integer> d;

    private ParserTranslations() {}

    private static <T extends Enum<T>> void mustContainAll(Map<T, Integer> map, Class<T> enumType) {
        T[] enumConstants = enumType.getEnumConstants();
        if(!map.keySet().containsAll(Arrays.asList(enumConstants))) {
            throw new IllegalStateException("not everything is mapped");
        }
    }

    static {
        e = new EnumMap<>(ErrorCode.class);
        p = new EnumMap<>(PropertyName.class);
        d = new EnumMap<>(PropertyValue.Predefined.class);

        e.put(ErrorCode.NOT_WELL_FORMED, R.string.parse_NOT_WELL_FORMED);
        e.put(ErrorCode.BAD_SYNTAX, R.string.parse_BAD_SYNTAX);
        e.put(ErrorCode.USAGE_NOT_ALLOWED, R.string.parse_USAGE_NOT_ALLOWED);
        e.put(ErrorCode.NO_URL, R.string.parse_NO_URL);
        e.put(ErrorCode.INVALID_METHOD, R.string.parse_INVALID_METHOD);
        e.put(ErrorCode.INTERNAL_ERROR, R.string.parse_INTERNAL_ERROR);

        p.put(PropertyName.DESCRIPTION, R.string.description);
        p.put(PropertyName.CONTACT, R.string.contact);
        p.put(PropertyName.LONG_NAME, R.string.long_name);
        p.put(PropertyName.DEVELOPER, R.string.developer);
        p.put(PropertyName.ATTRIBUTION, R.string.attribution);
        p.put(PropertyName.ADULT_CONTENT, R.string.adult_content);
        p.put(PropertyName.SEARCH_FORM, R.string.search_form);

        d.put(PropertyValue.Predefined.YES, R.string.yes);
        d.put(PropertyValue.Predefined.NO, R.string.no);

        mustContainAll(e, ErrorCode.class);
        mustContainAll(p, PropertyName.class);
        mustContainAll(d, PropertyValue.Predefined.class);
    }

    @StringRes static int getErrorCode(ErrorCode errorCode) {
        return Objects.requireNonNull(e.get(errorCode), "not found: " + errorCode);
    }
    @StringRes static int getPropertyName(PropertyName prop) {
        return Objects.requireNonNull(p.get(prop), "not found: " + prop);
    }
    @StringRes static int getPropertyValue(PropertyValue.Predefined value) {
        return Objects.requireNonNull(d.get(value), "not found: " + value);
    }
}
