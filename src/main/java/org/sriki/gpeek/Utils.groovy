package org.sriki.gpeek

static boolean hasPropertyNamed(obj, String name) {
    obj.metaClass.properties.any { it.name == name }
}

