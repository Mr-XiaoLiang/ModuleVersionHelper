package com.lollipop.mvh.git

import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import com.lollipop.mvh.data.MvhConfig
import com.lollipop.mvh.data.ProjectInfo
import org.eclipse.jgit.api.TransportCommand
import org.eclipse.jgit.api.TransportConfigCallback
import org.eclipse.jgit.transport.SshTransport
import org.eclipse.jgit.transport.Transport
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.eclipse.jgit.transport.ssh.jsch.JschConfigSessionFactory
import org.eclipse.jgit.transport.ssh.jsch.OpenSshConfig
import org.eclipse.jgit.util.FS
import java.io.File


class SshFactory(
    val projectInfo: ProjectInfo
) : JschConfigSessionFactory() {

    override fun createDefaultJSch(fs: FS?): JSch? {
        val jsch = super.createDefaultJSch(fs) ?: return null
        val sshKeyPath = projectInfo.sshKeyPath
        val sshHostPath = projectInfo.sshHostPath
        if (sshHostPath.isNotEmpty()) {
            jsch.setKnownHosts(sshHostPath)
        } else {
            setDefaultHost(jsch)
        }
        bindSshIdentity(jsch)
        if (sshKeyPath.isNotEmpty()) {
            jsch.addIdentity(sshKeyPath)
        }
        return jsch
    }

    private fun setDefaultHost(jsch: JSch) {
        val sshHome = MvhConfig.optSshHome()
        val homeFile = File(sshHome)
        jsch.setKnownHosts(File(homeFile, "known_hosts").path)
    }

    private fun bindSshIdentity(jsch: JSch) {
        val sshHome = MvhConfig.optSshHome()
        val homeFile = File(sshHome)
        homeFile.listFiles()?.forEach { file ->
            val fileName = file.name
            if (fileName.startsWith("id_") && !fileName.endsWith(".pub")) {
                jsch.addIdentity(file.path)
            }
        }
    }

    override fun configure(hc: OpenSshConfig.Host?, session: Session?) {
        super.configure(hc, session)
        session?.setConfig("StrictHostKeyChecking", "no");
    }

    class ConfigCallback(
        val projectInfo: ProjectInfo
    ) : TransportConfigCallback {

        private val sshFactory by lazy {
            SshFactory(projectInfo)
        }

        override fun configure(transport: Transport?) {
            if (transport is SshTransport) {
                transport.sshSessionFactory = sshFactory
            }
        }

    }

}

inline fun <reified T : TransportCommand<*, *>> T.bindAuth(projectInfo: ProjectInfo): T {
    this.setTransportConfigCallback(SshFactory.ConfigCallback(projectInfo))
    if (projectInfo.userEmail.isNotEmpty()) {
        this.setCredentialsProvider(
            UsernamePasswordCredentialsProvider(
                projectInfo.userEmail,
                projectInfo.userPwd
            )
        )
    }
    return this
}
