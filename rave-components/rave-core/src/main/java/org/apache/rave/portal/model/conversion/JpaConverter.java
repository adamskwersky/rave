package org.apache.rave.portal.model.conversion;

import org.apache.rave.model.ModelConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Converts objects into their JPA representations by delegating to wired in components
 */
@Component
public class JpaConverter {
    //Workaround for inability to access spring context without a lot of machinery
    //Will allow for a getInstance method to be called.  this is needed because the
    //Converters are all Spring beans with their own dependencies.
    private static JpaConverter instance;

    Map<Class<?>, Converter> converterMap;

    @Autowired
    public JpaConverter(List<ModelConverter> converters) {
        converterMap = new HashMap<Class<?>, Converter>();
        for(ModelConverter converter : converters) {
            converterMap.put(converter.getSourceType(), converter);
        }
        instance = this;
    }

    @SuppressWarnings("unchecked")
    public <S, T> T convert(S source, Class<S> clazz) {
        return (T)converterMap.get(clazz).convert(source);
    }

    @SuppressWarnings("unchecked")
    public <S,T> Converter<S, T> getConverter(Class<S> clazz) {
        return converterMap.get(clazz);
    }

    public static JpaConverter getInstance() {
        if(instance == null) {
            throw new IllegalStateException("Conversion factory not set by the Spring context");
        }
        return instance;
    }

}
