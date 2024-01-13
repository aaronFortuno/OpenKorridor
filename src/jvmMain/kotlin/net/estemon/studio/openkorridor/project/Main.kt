package net.estemon.studio.openkorridor.project

import io.ktor.server.application.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.routing.*
import io.kvision.remote.applyRoutes
import io.kvision.remote.getAllServiceManagers
import io.kvision.remote.kvisionInit
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("net.estemon.studio.openkorridor.project")
class PingModule

fun Application.main() {
    install(Compression)
    routing {
        getAllServiceManagers().forEach { applyRoutes(it) }
    }
    kvisionInit(PingModule().module)
}
