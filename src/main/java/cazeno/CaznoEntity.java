package cazeno;

import java.util.Random;
import java.util.UUID;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.network.protocol.AddItemEntityPacket;
import cn.nukkit.network.protocol.AddPlayerPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import cn.nukkit.scheduler.Task;
import me.onebone.economyapi.EconomyAPI;

public class CaznoEntity {
	Position pos = null;

	int money = 0;
	long AllMoney = 0;
	Integer psent;
	private final long eid;
	private final long itemeid;

	public CaznoEntity(Position pos, int money, long AllMoney, int psent) {
		this.eid = Entity.entityCount++;
		this.pos = pos;
		this.money = money;
		this.AllMoney = (long) AllMoney;
		this.psent = psent;

		this.itemeid = Entity.entityCount++;
		Server.getInstance().getScheduler().scheduleDelayedRepeatingTask(new Task() {
			@Override
			public void onRun(int currentTick) {
				update();
			}
		}, 20 * 5, 20 * 5);
	}

	public void itemspawn(Player player) {
		if (pos.getLevel().getFolderName().equals(player.getLevel().getFolderName())) {
			AddItemEntityPacket p = new AddItemEntityPacket();
			p.item = new Item(Item.GOLD_INGOT, 0, 1, "");
			p.entityRuntimeId = itemeid;
			p.entityUniqueId = itemeid;
			p.x = (float) (pos.getFloorX() + 0.5f);
			p.y = (float) pos.getFloorY() + 0.5f;
			p.z = (float) pos.getFloorZ() + 0.5f;
			p.speedX = 0;
			p.speedY = 0;
			p.speedZ = 0;
			player.dataPacket(p);
		}
	}

	public void spawnTo(Player player) {
		if (pos.getLevel().getFolderName().equals(player.getLevel().getFolderName())) {
			AddPlayerPacket pk = new AddPlayerPacket();
			pk.entityRuntimeId = eid;
			pk.entityUniqueId = eid;
			pk.uuid = UUID.randomUUID();
			pk.x = (float) (pos.getFloorX() + 0.5f);
			pk.y = (float) pos.getFloorY() + 0.5f;
			pk.z = (float) pos.getFloorZ() + 0.5f;
			pk.speedX = 0;
			pk.speedY = 0;
			pk.speedZ = 0;
			pk.yaw = 0;
			pk.pitch = 0;
			pk.item = Item.get(0);
			pk.username = " ";
			long flags = 0;
			flags |= 1 << Entity.DATA_FLAG_INVISIBLE;
			flags |= 1 << Entity.DATA_FLAG_CAN_SHOW_NAMETAG;
			flags |= 1 << Entity.DATA_FLAG_ALWAYS_SHOW_NAMETAG;
			flags |= 1 << Entity.DATA_FLAG_IMMOBILE;
			flags |= 1 << Entity.DATA_FLAG_NO_AI;
			EntityMetadata meta = new EntityMetadata();
			meta.putLong(Entity.DATA_FLAGS, flags);
			meta.putString(Entity.DATA_NAMETAG, "§e§l[ 도박 ]§r\n§l§f가격[총 금액] : §e" + money + "[" + AllMoney + "]");
			meta.putLong(Entity.DATA_LEAD_HOLDER_EID, -1);
			meta.putByte(Entity.DATA_LEAD, 0);
			meta.putFloat(Entity.DATA_SCALE, 0.001f);
			pk.metadata = meta;
			player.dataPacket(pk);
			itemspawn(player);

		}
	}

	public void spawnToAll() {
		Server.getInstance().getOnlinePlayers().forEach((uuid, player) -> {
			spawnTo(player);
		});
	}

	public void despawnFrom(Player player) {
		RemoveEntityPacket pk = new RemoveEntityPacket();
		pk.eid = eid;
		player.dataPacket(pk);
		RemoveEntityPacket pks = new RemoveEntityPacket();
		pks.eid = itemeid;
		player.dataPacket(pks);
	}

	public void despawnFromAll() {
		Server.getInstance().getOnlinePlayers().forEach((uuid, player) -> {
			despawnFrom(player);
		});
	}

	public Position getPosition() {
		return pos;
	}

	public Block getBlock() {
		return pos.getLevelBlock();
	}

	public void start(Player player) {
		double userMoney = EconomyAPI.getInstance().myMoney(player);
		if (userMoney < money) {
			player.sendMessage(Cazno.command("잔액이 부족합니다"));
			return;
		}
		int ran = new Random().nextInt(100);

		if (ran < this.psent) {

			EconomyAPI.getInstance().addMoney(player, AllMoney - AllMoney / 10);
			player.sendMessage(Cazno.success("§f도박에 성공하셨습니다 §e[" + AllMoney + "§c - 세금(" + AllMoney / 10 + ")§e]"));
			pos.getLevel().getPlayers().values()
					.forEach((a) -> a.sendMessage(Cazno.success(player.getName() + "님이 도박에 성공하셨습니다")));
			this.AllMoney = 0;

		} else {
			EconomyAPI.getInstance().reduceMoney(player, money);
			this.AllMoney += money;
			player.sendMessage(Cazno.alert("도박에 실패하셨습니다 "));
		}

	}

	public void setAllMoney(long allmoney) {
		this.AllMoney = allmoney;
	}

	public void update() {
		Server.getInstance().getOnlinePlayers().values().forEach((player) -> {
			if (player.distance(getPosition()) < 30) {
				this.spawnTo(player);
			} else {
				this.despawnFrom(player);
			}
		});

	}

}
