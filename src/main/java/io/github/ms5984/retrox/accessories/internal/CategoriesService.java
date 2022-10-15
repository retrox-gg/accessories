package io.github.ms5984.retrox.accessories.internal;
/*
 *  Copyright 2022 ms5984, Retrox
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import io.github.ms5984.retrox.accessories.api.Category;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.PropertyResourceBundle;

final class CategoriesService {
    private final LinkedHashMap<String, CategoryImpl> categories = new LinkedHashMap<>();

    CategoriesService() {}

    void loadCategories() {
        if (!categories.isEmpty()) categories.clear();
        final var bundle = PropertyResourceBundle.getBundle("categories");
        final var keys = bundle.getKeys();
        while (keys.hasMoreElements()) {
            final var key = keys.nextElement();
            final var category = new CategoryImpl(bundle.getString(key));
            categories.put(key, category);
        }
    }

    public Iterator<? extends Category> iterator() {
        return categories.values().iterator();
    }
}
