package com.ah.be.rest.util;

import java.io.Writer;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import com.thoughtworks.xstream.io.json.JsonWriter;
import com.thoughtworks.xstream.io.json.JsonWriter.Format;

public class FormatedJsonHierarchicalStreamDriver extends
		JsonHierarchicalStreamDriver {

	@Override
	public HierarchicalStreamWriter createWriter(Writer out) {
		return new JsonWriter(out, 0, new Format(new char[0], new char[0],
				Format.COMPACT_EMPTY_ELEMENT));
	}
}
