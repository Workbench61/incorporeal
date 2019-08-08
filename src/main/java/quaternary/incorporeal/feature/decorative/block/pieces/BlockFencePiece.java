package quaternary.incorporeal.feature.decorative.block.pieces;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class BlockFencePiece extends BlockFence {
	public BlockFencePiece(Block block, Material mat, MapColor color, boolean cutout) {
		super(mat, color);
		this.cutout = cutout;
		this.mainBlock = block;
		
		setSoundType(block.getSoundType());
	}
	
	private final Block mainBlock;
	private final boolean cutout;
	
	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getRenderLayer() {
		return cutout ? BlockRenderLayer.CUTOUT_MIPPED : BlockRenderLayer.SOLID;
	}
	
	@Override
	public boolean isToolEffective(String type, IBlockState state) {
		return mainBlock.isToolEffective(type, state);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag mistake) {
		mainBlock.addInformation(stack, worldIn, tooltip, mistake);
	}
}