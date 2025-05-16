package ru.zxspectrum.io.tzx;

public final class BlockFactory {
    public static boolean isBlockSupported(int id) {
        switch (id) {
            case DataBlock.DEFAULT_ID:
            case TextDescriptionBlock.DEFAULT_ID:
            case PauseBlock.DEFAULT_ID:
            case MessageBlock.DEFAULT_ID:
            case ArchiveInfoBlock.DEFAULT_ID:
            case StopTape48kBlock.DEFAULT_ID:
            case GroupStartBlock.DEFAULT_ID:
            case PureToneBlock.DEFAULT_ID:
            case PureSequenceBlock.DEFAULT_ID:
            case PureDataBlock.DEFAULT_ID:
                return true;

        }
        return false;
    }

    public static Block construct(int id) {
        if (!isBlockSupported(id)) {
            throw new IllegalArgumentException("id=" + id);
        }
        Block block = null;
        switch (id) {
            case DataBlock.DEFAULT_ID:
                block = new DataBlock();
                break;
            case TextDescriptionBlock.DEFAULT_ID:
                block = new TextDescriptionBlock();
                break;
            case PauseBlock.DEFAULT_ID:
                block = new PauseBlock();
                break;
            case MessageBlock.DEFAULT_ID:
                block = new MessageBlock();
                break;
            case ArchiveInfoBlock.DEFAULT_ID:
                block = new ArchiveInfoBlock();
                break;
            case StopTape48kBlock.DEFAULT_ID:
                block = new StopTape48kBlock();
                break;
            case GroupStartBlock.DEFAULT_ID:
                block = new GroupStartBlock();
                break;
            case PureToneBlock.DEFAULT_ID:
                block = new PureToneBlock();
                break;
            case PureSequenceBlock.DEFAULT_ID:
                block = new PureSequenceBlock();
                break;
            case PureDataBlock.DEFAULT_ID:
                block = new PureDataBlock();
                break;
        }
        return block;
    }
}
