package org.oreon.gl.demo.oreonworlds.shaders.assets.plants;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.List;

import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.instanced.GLInstancedCluster;
import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.instanced.InstancedCluster;
import org.oreon.core.math.Matrix4f;
import org.oreon.core.model.Material;
import org.oreon.core.scenegraph.ComponentType;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ResourceLoader;

public class PalmBillboardShader extends GLShader{

	private static PalmBillboardShader instance = null;
	
	public static PalmBillboardShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new PalmBillboardShader();
	    }
	      return instance;
	}
	
	protected PalmBillboardShader()
	{
		super();
		
		addVertexShader(ResourceLoader.loadShader("oreonworlds/shaders/assets/Palm_Shader/Palm01_VS.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/shaders/assets/Palm_Shader/Palm01_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/shaders/assets/Palm_Shader/Palm01Billboard_FS.glsl"));
		compileShader();
		
		addUniform("clipplane");
		addUniformBlock("worldMatrices");
		addUniformBlock("modelMatrices");
		addUniformBlock("Camera");
		addUniform("material.diffusemap");
		addUniform("scalingMatrix");
		addUniform("isReflection");
		
		for (int i=0; i<100; i++)
		{
			addUniform("matrixIndices[" + i + "]");
		}
	}
	
	public void updateUniforms(Renderable object){
		
		setUniform("clipplane", EngineContext.getCommonConfig().getClipplane());
		bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
		setUniformi("isReflection", EngineContext.getCommonConfig().isReflection() ? 1 : 0);
		setUniform("scalingMatrix", new Matrix4f().Scaling(object.getWorldTransform().getScaling()));
		
		((GLInstancedCluster) object.getParent()).getWorldMatricesBuffer().bindBufferBase(0);
		bindUniformBlock("worldMatrices", 0);
		((GLInstancedCluster) object.getParent()).getModelMatricesBuffer().bindBufferBase(1);
		bindUniformBlock("modelMatrices", 1);
		
		Material material = (Material) object.getComponent(ComponentType.MATERIAL0);
		
		glActiveTexture(GL_TEXTURE0);
		material.getDiffusemap().bind();
		setUniformi("material.diffusemap", 0);
		
		List<Integer> indices = ((InstancedCluster) object.getParent()).getLowPolyIndices();
		
		for (int i=0; i<indices.size(); i++)
		{
			setUniformi("matrixIndices[" + i +"]", indices.get(i));	
		}
	}
}
