package com.lollipop.mvh.git

import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import com.lollipop.mvh.data.MvhConfig
import org.eclipse.jgit.api.TransportCommand
import org.eclipse.jgit.api.TransportConfigCallback
import org.eclipse.jgit.transport.SshTransport
import org.eclipse.jgit.transport.Transport
import org.eclipse.jgit.transport.ssh.jsch.JschConfigSessionFactory
import org.eclipse.jgit.transport.ssh.jsch.OpenSshConfig
import org.eclipse.jgit.util.FS
import java.io.File


object SshFactory : JschConfigSessionFactory() {

    override fun createDefaultJSch(fs: FS?): JSch? {
        val jsch = super.createDefaultJSch(fs)
        val sshHome = MvhConfig.optSshHome()
        val homeFile = File(sshHome)
        jsch.setKnownHosts(File(homeFile, "known_hosts").path)
        homeFile.listFiles()?.forEach { file ->
            val fileName = file.name
            if (fileName.startsWith("id_") && !fileName.endsWith(".pub")) {
                jsch.addIdentity(file.path)
            }
        }
        return jsch
    }

    override fun configure(hc: OpenSshConfig.Host?, session: Session?) {
        super.configure(hc, session)
        session?.setConfig("StrictHostKeyChecking", "no");
    }

    fun createConfigCallback(): TransportConfigCallback {
        return ConfigCallback()
    }

    class ConfigCallback : TransportConfigCallback {
        override fun configure(transport: Transport?) {
            if (transport is SshTransport) {
                transport.sshSessionFactory = SshFactory
            }
        }

    }

}

inline fun <reified T : TransportCommand<*, *>> T.bindSsh(): T {
    this.setTransportConfigCallback(SshFactory.createConfigCallback())
    return this
}
