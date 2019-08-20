package cn.hutool.extra.ssh;

import org.junit.Ignore;
import org.junit.Test;

import com.jcraft.jsch.Session;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Console;

/**
 * Jsch工具类单元测试
 * 
 * @author looly
 *
 */
public class JschUtilTest {
	
	@Test
	@Ignore
	public void bindPortTest() {
		//新建会话，此会话用于ssh连接到跳板机（堡垒机），此处为10.1.1.1:22
		Session session = JschUtil.getSession("looly.centos", 22, "test", "123456");
		// 将堡垒机保护的内网8080端口映射到localhost，我们就可以通过访问http://localhost:8080/访问内网服务了
		JschUtil.bindPort(session, "172.20.12.123", 8080, 8080);
	}
	
	@Test
	@Ignore
	public void sftpTest() {
		Session session = JschUtil.getSession("looly.centos", 22, "root", "123456");
		Sftp sftp = JschUtil.createSftp(session);
		sftp.mkDirs("/opt/test/aaa/bbb");
		Console.log("OK");
	}
	
	@Test
	@Ignore
	public void reconnectIfTimeoutTest() throws InterruptedException {
		Session session = JschUtil.getSession("sunnyserver", 22,"mysftp","liuyang1234");
		Sftp sftp = JschUtil.createSftp(session);

		Console.log("打印pwd: " + sftp.pwd());
		Console.log("cd / : " + sftp.cd("/"));
		Console.log("休眠一段时间，查看是否超时");
		Thread.sleep(30 * 1000);

		try{
			// 当连接超时时，isConnected()仍然返回true，pwd命令也能正常返回，因此，利用发送cd命令的返回结果，来判断是否连接超时
			Console.log("isConnected " + sftp.getClient().isConnected());
			Console.log("打印pwd: " + sftp.pwd());
			Console.log("cd / : " + sftp.cd("/"));
		}catch (JschRuntimeException e) {
			e.printStackTrace();
		}

		Console.log("调用reconnectIfTimeout方法，判断是否超时并重连");
		sftp.reconnectIfTimeout();

		Console.log("打印pwd: " + sftp.pwd());

		IoUtil.close(sftp);
	}
}
