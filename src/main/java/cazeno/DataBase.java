package cazeno;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;

public class DataBase {
	private static DataBase instance = null;
	LinkedHashMap<Level, List<CaznoEntity>> caznoList = new LinkedHashMap<>();

	public DataBase(Cazno plugin) throws IOException {
		plugin.getDataFolder().getPath();
		for (File f : new File(Server.getInstance().getDataPath() + "/worlds").listFiles()) {
			new File(f.getPath(), "cazno").mkdirs();
			if (new File(f.getPath(), "cazno").listFiles().length < 1) {
				break;
			}
			for (File file : new File(f.getPath(), "cazno").listFiles()) {
				CompoundTag tag = null;

				tag = NBTIO.read(file);

				Position pos = new Position(tag.getFloat("x"), tag.getFloat("y"), tag.getFloat("z"),
						Server.getInstance().getLevelByName(tag.getString("level")));
				int money = tag.getInt("money");
				long allmoney = tag.getLong("allmoney");
				int psent = tag.getInt("psent");
				registerCazno(pos, money, allmoney, psent);
			}

		}
		instance = this;
	}

	public static String toString(Position pos) {
		StringBuffer str = new StringBuffer();
		str.append(pos.getFloorX() + "").append("_" + pos.getFloorY()).append("_" + pos.getFloorZ());
		return str.toString();
	}

	public void registerCazno(Position pos, int money, long allMoney, int psent) {
		if (caznoList.getOrDefault(pos.getLevel(), new ArrayList<>()).isEmpty()) {
			List<CaznoEntity> list = new ArrayList<>();
			caznoList.put(pos.getLevel(), list);
		}
		caznoList.get(pos.getLevel()).add(new CaznoEntity(pos, money, allMoney, psent));
	}

	public CaznoEntity getCaznobyPosition(Position pos) {
		for (CaznoEntity e : this.caznoList.get(pos.level)) {
			if (e.getBlock().equals(pos.getLevelBlock())) {
				return e;
			}
		}
		return null;

	}

	public void deregisterCazno(Position pos) {
		caznoList.get(pos.getLevel()).forEach((e) -> {
			if (e.getBlock() == pos.getLevelBlock())
				caznoList.get(pos.getLevel()).remove(e);
		});
	}

	public static DataBase getInstance() {
		return instance;
	}

	public void save() throws IOException {
		for (Level l : caznoList.keySet()) {
			File f = new File(Server.getInstance().getDataPath() + "/worlds", l.getFolderName());
			File file = new File(f, "cazno");
			file.mkdirs();
			for (File fi : file.listFiles()) {
				fi.delete();
			}
			for (CaznoEntity tag : caznoList.get(l)) {

				NBTIO.write(new CompoundTag().putFloat("x", (float) tag.getPosition().x)
						.putFloat("y", (float) tag.getPosition().y).putFloat("z", (float) tag.getPosition().z)
						.putString("level", tag.getPosition().getLevel().getFolderName()).putInt("money", tag.money)
						.putLong("allmoney", tag.AllMoney).putInt("psent", tag.psent),
						new File(file, toString(tag.getPosition()) + ".dat"));

			}
		}
	}
}
