CREATE TABLE [dbo].[users](
    [id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
    [username] [nvarchar](100) NOT NULL UNIQUE,
    [password] [nvarchar](255) NOT NULL,
    [email] [nvarchar](150) NOT NULL UNIQUE,
    [full_name] [nvarchar](150) NULL,
    [phone_number] [nvarchar](50) NULL,
    [enabled] [bit] NOT NULL DEFAULT 1,
    [account_non_locked] [bit] NOT NULL DEFAULT 1,
    [password_expiry_date] [datetime2](7) NULL,
    [created_at] [datetime2](7) NOT NULL DEFAULT GETDATE(),
    [last_login] [datetime2](7) NULL,
    [role] [nvarchar](50) NOT NULL DEFAULT 'ROLE_USER'
);
