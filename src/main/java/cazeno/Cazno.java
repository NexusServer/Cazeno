package cazeno;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandMap;
import cn.nukkit.command.SimpleCommandMap;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.plugin.PluginBase;
import commands.AddCaznoCommand;

public class Cazno extends PluginBase implements Listener {
	public static HashMap<Player, HashMap<String, Object>> addList = new HashMap();
	public static List<Player> delList = new ArrayList<>();

	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getServer().getCommandMap().register("caznooperator", new AddCaznoCommand());
		try {
			new DataBase(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void addCazno(PlayerInteractEvent event) {
		if (addList.containsKey(event.getPlayer())) {
			DataBase.getInstance().registerCazno(event.getBlock(), (int) addList.get(event.getPlayer()).get("money"), 0,
					(int) addList.get(event.getPlayer()).get("psent"));
			addList.remove(event.getPlayer());
			event.getPlayer().sendMessage(success("정상적으로 도박장이 생성되었습니다"));
		}
	}

	@EventHandler
	public void onCazno(PlayerInteractEvent event) {
		try {
			if (DataBase.getInstance().getCaznobyPosition(event.getBlock()) != null) {
				DataBase.getInstance().getCaznobyPosition(event.getBlock()).start(event.getPlayer());
			}
		} catch (Exception e) {
			return;
		}
	}

	public static String message(String message) {
		return "§a§l[알림] §r§7" + message;
	}

	public static String alert(String message) {
		return "§c§l[알림] §r§7" + message;
	}

	public static String command(String message) {
		return "§l§6[알림]§r§7 " + message;
	}

	public static String success(String message) {
		return "§l§b[안내]§r§7 " + message;
	}

	@Override
	public void onDisable() {
		try {
			DataBase.getInstance().save();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
