package enhancedportals.portal.network;

import net.minecraft.server.MinecraftServer;
import enhancedcore.world.WorldPosition;
import enhancedportals.lib.Reference;

public class DialDeviceNetwork extends NetworkManager
{
    public DialDeviceNetwork(MinecraftServer server)
    {
        super(server);
    }

    @Override
    public void addToNetwork(String key, WorldPosition data)
    {
        if (!hasNetwork(key) || getNetwork(key).isEmpty())
        {
            super.addToNetwork(key, data);
        }
    }

    @Override
    public String getSaveFileName()
    {
        return Reference.MOD_ID + "_DialDevices.dat";
    }
}
