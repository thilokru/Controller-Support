package com.mhfs.controller.mappings;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mhfs.controller.mappings.actions.ActionRegistry;
import com.mhfs.controller.mappings.actions.IAction;
import com.mhfs.controller.mappings.conditions.ICondition;

import net.minecraft.util.ResourceLocation;

public class GsonHelper {
	
	public static Gson getGson(GsonBuilder builder) {
		builder.enableComplexMapKeySerialization();
		builder.registerTypeHierarchyAdapter(ICondition.class, new ConditionTypeAdapter());
		builder.registerTypeHierarchyAdapter(IAction.class, new ActionTypeAdapter());
		builder.registerTypeHierarchyAdapter(StickConfig.class, new StickConfigTypeAdapter());
		builder.registerTypeHierarchyAdapter(ResourceLocation.class, new ResourceLocationTypeAdapter());
		return builder.create();
	}
	
	public static Gson getExposedGson() {
		return getGson(new GsonBuilder().excludeFieldsWithoutExposeAnnotation());
	}
	
	public static class ConditionTypeAdapter extends TypeAdapter<ICondition> {
		@Override
		public void write(JsonWriter out, ICondition value) throws IOException {
			out.value(ConditionSerializationHelper.toString(value));
		}

		@Override
		public ICondition read(JsonReader in) throws IOException {
			return ConditionSerializationHelper.fromString(in.nextString());
		}
	}
	
	public static class ActionTypeAdapter extends TypeAdapter<IAction> {
		
		@Override
		public void write(JsonWriter out, IAction value) throws IOException {
			out.value(value.getActionName());
		}

		@Override
		public IAction read(JsonReader in) throws IOException {
			String actionName = in.nextString();
			IAction res = ActionRegistry.getAction(actionName);
			if(res == null) {
				throw new RuntimeException("Unknown action: " + actionName);
			}
			return res;
		}
	}
	
	public static class ResourceLocationTypeAdapter extends TypeAdapter<ResourceLocation> {

		@Override
		public void write(JsonWriter out, ResourceLocation value) throws IOException {
			out.value(value.toString());
		}

		@Override
		public ResourceLocation read(JsonReader in) throws IOException {
			return new ResourceLocation(in.nextString());
		}
	}
	
	public static class StickConfigTypeAdapter extends TypeAdapter<StickConfig> {
		
		@Override
		public void write(JsonWriter out, StickConfig value) throws IOException {
			out.value(ControllInfo.get().getStickName(value));
		}
		
		@Override
		public StickConfig read(JsonReader in) throws IOException {
			return ControllInfo.get().getStick(in.nextString());
		}
	}
}
