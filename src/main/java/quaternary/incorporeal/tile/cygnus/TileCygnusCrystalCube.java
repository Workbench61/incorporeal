package quaternary.incorporeal.tile.cygnus;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import quaternary.incorporeal.Incorporeal;
import quaternary.incorporeal.api.cygnus.ICygnusStack;
import quaternary.incorporeal.block.cygnus.BlockCygnusCrystalCube;
import quaternary.incorporeal.cygnus.IncorporeticCygnusActions;
import quaternary.incorporeal.cygnus.IncorporeticCygnusConditions;
import quaternary.incorporeal.entity.cygnus.EntityCygnusMasterSpark;
import quaternary.incorporeal.etc.helper.CygnusHelpers;

import java.util.function.Predicate;

public class TileCygnusCrystalCube extends TileCygnusBase implements ITickable {
	private boolean enabled;
	private long lastUpdate = 0;
	
	private Predicate<ICygnusStack> condition = IncorporeticCygnusConditions.NOTHING;
	
	@Override
	public void update() {
		lastUpdate = world.getTotalWorldTime();
		
		EntityCygnusMasterSpark master = CygnusHelpers.getMasterSparkForSparkAt(world, pos);
		if(master != null) {
			boolean wasEnabled = enabled;
			enabled = condition.test(master.getCygnusStack());
			if(enabled != wasEnabled) {
				IBlockState s = world.getBlockState(pos);
				Block b = s.getBlock();
				if(!(b instanceof BlockCygnusCrystalCube)) return;
				
				world.setBlockState(pos, s.withProperty(BlockCygnusCrystalCube.POWERED, enabled));
				world.updateComparatorOutputLevel(pos, b);
			}
		}
	}
	
	public Predicate<ICygnusStack> getCondition() {
		return condition;
	}
	
	public void setCondition(Predicate<ICygnusStack> condition) {
		this.condition = condition;
	}
	
	public boolean isEnabled() {
		if(lastUpdate != world.getTotalWorldTime()) update();
		return enabled;
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setString("Condition", Incorporeal.API.getCygnusStackConditionRegistry().nameOf(condition).toString());
		return super.writeToNBT(nbt);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		condition = Incorporeal.API.getCygnusStackConditionRegistry().get(
						new ResourceLocation(nbt.getString("Condition"))
		);
		if(condition == null) condition = IncorporeticCygnusConditions.NOTHING;
	}
}
