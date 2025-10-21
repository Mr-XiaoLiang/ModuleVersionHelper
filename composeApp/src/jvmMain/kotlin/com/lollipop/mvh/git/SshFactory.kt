package com.lollipop.mvh.git

import com.jcraft.jsch.JSch
import org.eclipse.jgit.api.TransportConfigCallback
import org.eclipse.jgit.transport.SshTransport
import org.eclipse.jgit.transport.Transport
import org.eclipse.jgit.transport.ssh.jsch.JschConfigSessionFactory
import org.eclipse.jgit.util.FS


object SshFactory : JschConfigSessionFactory() {

    override fun createDefaultJSch(fs: FS?): JSch? {
        return super.createDefaultJSch(fs)
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