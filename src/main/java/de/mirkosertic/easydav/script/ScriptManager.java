package de.mirkosertic.easydav.script;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mirkosertic.easydav.event.Event;
import de.mirkosertic.easydav.event.EventListener;

import java.util.HashSet;
import java.util.Set;

public class ScriptManager implements EventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptManager.class);    

    private final ScriptEngineManager scriptEngineManager;
    private final ScriptEngine scriptEngine;
    private final Set<String> undefinedHandlers;

    public ScriptManager() throws ScriptException {

        undefinedHandlers = new HashSet<>();

        scriptEngineManager = new ScriptEngineManager();
        scriptEngine = scriptEngineManager.getEngineByName("nashorn");

        scriptEngine.eval("function onFileCreatedOrUpdatedEvent(aEvent) {print('got event '+aEvent);}");
    }

    @Override
    public void handle(Event aEvent) {
        String theHandlerName = "on"+aEvent.getClass().getSimpleName();
        if (!undefinedHandlers.contains(theHandlerName)) {
            Invocable theInvokeable = (Invocable) scriptEngine;
            try {
                theInvokeable.invokeFunction(theHandlerName, aEvent);
            } catch (NoSuchMethodException e) {
                // No method found for invocation
                // So no eventhandler is defined
                undefinedHandlers.add(theHandlerName);

            } catch (ScriptException e) {
                LOGGER.error("Error executing script", e);
            }
        } else {
            LOGGER.debug("Skipping event {}, as no handler is defined", aEvent);
        }
    }
}
