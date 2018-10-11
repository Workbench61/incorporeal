package quaternary.incorporeal.block.cygnus;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import quaternary.incorporeal.api.cygnus.ICygnusFunnelable;
import quaternary.incorporeal.cygnus.CygnusStack;
import quaternary.incorporeal.entity.cygnus.EntityCygnusMasterSpark;
import quaternary.incorporeal.etc.helper.CygnusHelpers;
import vazkii.botania.api.state.BotaniaStateProps;

import javax.annotation.Nullable;
import java.util.List;

public class BlockCygnusFunnel extends BlockCygnusBase {
	public static final PropertyEnum<EnumFacing> FACING = BotaniaStateProps.FACING;
	public static final PropertyBool POWERED = BotaniaStateProps.POWERED;
	
	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos updaterPos) {
		boolean isPowered = state.getValue(POWERED);
		boolean shouldPower = world.isBlockPowered(pos);
		
		if(isPowered != shouldPower) {
			world.setBlockState(pos, state.withProperty(POWERED, shouldPower));
			if(shouldPower) {
				EnumFacing facing = state.getValue(FACING);
				BlockPos fromPos = pos.offset(facing);
				BlockPos toPos = pos.offset(facing.getOpposite());
				
				ICygnusFunnelable source = findCygnusFunnelable(world, fromPos);
				ICygnusFunnelable sink = findCygnusFunnelable(world, toPos);
				
				boolean sourceCanGive = source != null && source.canGiveCygnusItem();
				boolean sinkCanAccept = sink != null && sink.canAcceptCygnusItem();
				if(!sourceCanGive && !sinkCanAccept) return;
				
				if(sourceCanGive && sinkCanAccept) {
					//Move data from the source to the sink, no stack needed
					sink.acceptItemFromCygnus(source.giveItemToCygnus());
					return;
				}
				
				//Only 1 action is available (sourcing or sinking). So we will find a Cygnus stack
				//and use that as the other end of the action.
				EntityCygnusMasterSpark master = CygnusHelpers.getMasterSparkForSparkAt(world, pos);
				if(master == null) return; //Or not, since we're not even on a network apparently :p
				
				CygnusStack stack = master.getCygnusStack();
				if(sourceCanGive) {
					stack.push(source.giveItemToCygnus());
				} else {
					stack.pop().ifPresent(sink::acceptItemFromCygnus);
				}
			}
		}
	}
	
	@Nullable
	private static ICygnusFunnelable findCygnusFunnelable(World world, BlockPos pos) {
		//Is it a block (as an interface?)
		IBlockState state = world.getBlockState(pos);
		if(state.getBlock() instanceof ICygnusFunnelable) {
			return (ICygnusFunnelable) state;
		}
		
		//Is it a tile entity capability?
		TileEntity tile = world.getTileEntity(pos);
		if(tile != null) {
			ICygnusFunnelable capMaybe = tile.getCapability(null, null); //TODO actual cap
			if(capMaybe != null) return capMaybe;
		}
		
		//Is it an entity capability?
		List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos));
		for(Entity e : entities) {
			ICygnusFunnelable capMaybe = e.getCapability(null, null); //TODO actual cap
			if(capMaybe != null) return capMaybe;
		}
		
		//Idk where it is!
		return null;
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex() + (state.getValue(POWERED) ? 6 : 0);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.getFront(meta % 6)).withProperty(POWERED, meta >= 6);
	}
}
