package org.jbehave.core.story;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.NoSuchElementException;

import org.jbehave.core.story.codegen.domain.StoryDetails;
import org.jbehave.core.story.codegen.parser.StoryParser;
import org.jbehave.core.story.domain.Story;

/**
 * StoryLoader parses story details from a resource in the classpath and build a Story via the StoryBuilder.
 * 
 * @author Mauro Talevi
 * @see StoryBuilder
 */
public class StoryLoader {

    private ClassLoader classLoader;
    private StoryParser storyParser;

    public StoryLoader(StoryParser storyParser, ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.storyParser = storyParser;
    }

    public StoryDetails loadStoryDetails(String storyPath) {
        return storyParser.parseStory(getReader(storyPath, classLoader));
    }
    
    public Story loadStory(String storyPath) {
        StoryDetails storyDetails = loadStoryDetails(storyPath);
        return new StoryBuilder(storyDetails, classLoader).story();
    }

    public Story loadStory(Class storyClass) {
        return loadStoryClass(storyClass.getName());        
    }

    public Story loadStoryClass(String storyClassName) {
        try {
            return (Story) classLoader.loadClass(storyClassName).newInstance();
        } catch ( Exception e) {
            throw new InvalidStoryClassException("Failed to load story for class "+storyClassName, e);
        }
    }
    
    protected Reader getReader(String resource, ClassLoader classLoader) {
        InputStream is = classLoader.getResourceAsStream(resource);
        if ( is == null ){
            throw new NoSuchElementException("Resource "+resource+" not found in ClassLoader "+classLoader.getClass());
        }
        return new InputStreamReader(is);
    }
    
    static class InvalidStoryClassException extends RuntimeException {
        public InvalidStoryClassException(String message, Exception cause) {
            super(message, cause);
        }        
    }
}
