package org.oms.itch.generator;

import org.oms.itch.generator.impl.IndexFeedGenerator;
import org.oms.itch.generator.impl.NewsFeedGenerator;
import org.oms.itch.generator.impl.TotalviewMessageGenerator;
import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.TypeConversionException;

public class ITCHGeneratorConverter implements ITypeConverter<ITCHMessageGenerator> {
    @Override
    public ITCHMessageGenerator convert(String value) throws Exception {
        if ("total".equalsIgnoreCase(value)) {
            return new TotalviewMessageGenerator();
        } else if ("news".equalsIgnoreCase(value)) {
            return new NewsFeedGenerator();
        } else if ("index".equalsIgnoreCase(value)) {
            return new IndexFeedGenerator();
        } else {
            throw new TypeConversionException("Invalid value for ITCH Generator. Valid values are: total, news, index");
        }
    }
}