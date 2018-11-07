package com.mindera.skeletoid.logs.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.FileProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({FileProvider.class, Intent.class})
public class ShareLogFilesUtilsUnitTests {

    @Test
    public void testGetFileLogPath() {
        Context context = mock(Context.class);

        File file = mock(File.class);
        when(context.getFilesDir()).thenReturn(file);
        when(file.getPath()).thenReturn("/com/mindera/skeletoid");

        assertEquals("/com/mindera/skeletoid", ShareLogFilesUtils.getFileLogPath(context));
    }
}
