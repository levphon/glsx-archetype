package com.glsx.plat.dfs.config;

import com.github.tobato.fastdfs.FdfsClientConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(FdfsClientConfig.class)
public class DfsConfig {
}